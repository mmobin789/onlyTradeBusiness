package onlytrade.app.viewmodel.product.offer.repository

import com.russhwolf.settings.Settings
import io.ktor.http.HttpStatusCode
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.until
import kotlinx.serialization.json.Json
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.offer.repository.data.OfferMapper.toOffer
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer
import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.AcceptOfferApi
import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.AddOfferApi
import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.CompleteOfferApi
import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.DeleteOfferApi
import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.GetOffersApi
import onlytrade.app.viewmodel.product.offer.repository.data.remote.request.AddOfferRequest
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.AcceptOfferResponse
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.AddOfferResponse
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.CompleteOfferResponse
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.DeleteOfferResponse
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.GetOffersResponse
import onlytrade.db.OnlyTradeDB

class OfferRepository(
    private val loginRepository: LoginRepository,
    private val addOfferApi: AddOfferApi,
    private val getOffersApi: GetOffersApi,
    private val deleteOfferApi: DeleteOfferApi,
    private val acceptOfferApi: AcceptOfferApi,
    private val completeOfferApi: CompleteOfferApi,
    private val localPrefs: Settings,
    private val onlyTradeDB: OnlyTradeDB
) {
    private val offersLastUpdatedAt = "OFFERS_LAST_UPDATED_AT"

    private val offerDao = onlyTradeDB.offerQueries

    private val productDao = onlyTradeDB.productQueries

    private suspend fun getOffersApi() = loginRepository.jwtToken()?.let { jwtToken ->
        getOffersApi.getOffers(jwtToken).also {
            if (it.offers.isNullOrEmpty()) offerDao.transaction { offerDao.deleteAll() }
            else {
                localPrefs.putString(offersLastUpdatedAt, Clock.System.now().toString())
                addOffers(it.offers)

            }
        }
    } ?: GetOffersResponse(
        statusCode = HttpStatusCode.Unauthorized.value,
        error = HttpStatusCode.Unauthorized.description
    )

    suspend fun getOffers() = localPrefs.getStringOrNull(offersLastUpdatedAt)?.run {
        val offersUpdateDateTime = Instant.parse(this)
        val now = Clock.System.now()
        val minutesDiff = offersUpdateDateTime.until(now, DateTimeUnit.MINUTE)
        val updateRequired =
            minutesDiff >= 1  // 2 minutes //todo need to update server sync time.

        if (updateRequired) {
            getOffersApi()
        } else {
            val localOffers = offerDao.transactionWithResult {
                offerDao.getOffers(false).executeAsList().map(::toOffer)
            }
            if (localOffers.isEmpty()) {
                getOffersApi()
            } else GetOffersResponse(offers = localOffers, statusCode = HttpStatusCode.OK.value)
        }
    } ?: getOffersApi()

    /**
     * The returns the 1st offer made by the user.
     * This method is offline only.
     */
    fun getOfferMade(offerMakerId: Long, offerReceiverProductId: Long) =
        offerDao.transactionWithResult {
            offerDao.getOfferMade(offerMakerId, offerReceiverProductId).executeAsOneOrNull()
                ?.run(::toOffer)
        }

    /**
     * The returns the 1st offer received by the user.
     * This method is offline only.
     */
    fun getOfferReceived(offerReceiverId: Long, offerReceiverProductId: Long) =
        offerDao.transactionWithResult {
            offerDao.getOfferReceived(offerReceiverId, offerReceiverProductId).executeAsOneOrNull()
                ?.run(::toOffer)
        }

    fun getOfferAccepted(offerId: Long) = offerDao.transactionWithResult {
        offerDao.getOfferAccepted(offerId, accepted = true, completed = false).executeAsOneOrNull()
            ?.run(::toOffer)
    }

    private fun getOfferCompleted(offerId: Long) = offerDao.transactionWithResult {
        offerDao.getOfferCompleted(offerId, true).executeAsOneOrNull()
            ?.run(::toOffer)
    }

    suspend fun addOffer(
        offerReceiverId: Long, offerReceiverProductId: Long, offeredProductIds: LinkedHashSet<Long>
    ) = loginRepository.jwtToken()?.let { jwtToken ->
        val offerMakerId = loginRepository.user()!!.id // This is guaranteed.
        getOfferMade(offerMakerId, offerReceiverProductId)?.let { offer ->
            AddOfferResponse(
                offer = offer,
                statusCode = HttpStatusCode.Created.value,
                error = HttpStatusCode.Created.description
            )
        } ?: let {
            val addOfferRequest = AddOfferRequest(
                offeredProductIds = offeredProductIds,
                offerMakerId = offerMakerId,
                offerReceiverId = offerReceiverId,
                offerReceiverProductId = offerReceiverProductId,
            )
            addOfferApi.addOffer(addOfferRequest, jwtToken).also {
                it.offer?.run {
                    addOffer(this)
                }
            }
        }
    } ?: AddOfferResponse(
        statusCode = HttpStatusCode.Unauthorized.value,
        error = HttpStatusCode.Unauthorized.description
    )

    private fun addOffer(offer: Offer) = offerDao.transaction {
        offer.run {
            offerDao.add(
                id = id,
                offerMakerId = offerMakerId,
                offerReceiverId = offerReceiverId,
                offerReceiverProductId = offerReceiverProduct.id,
                offerReceiverProduct = Json.encodeToString(offerReceiverProduct),
                offeredProducts = Json.encodeToString(offeredProducts),
                extraPrice = extraPrice,
                accepted = accepted,
                completed = completed
            )
        }
    }

    suspend fun withdrawOffer(offerMakerId: Long, offerReceiverProductId: Long) =
        loginRepository.jwtToken()?.let { jwtToken ->
            getOfferMade(offerMakerId, offerReceiverProductId)?.let { offer ->
                deleteOfferApi.deleteOffer(jwtToken, offer.id).also {
                    if (it.deletedOfferId != null || it.statusCode == HttpStatusCode.NotFound.value) {
                        deleteOfferUpdateProduct(offer.id, offerReceiverProductId)
                    }
                }
            } ?: DeleteOfferResponse(HttpStatusCode.OK.value)

        } ?: DeleteOfferResponse(
            statusCode = HttpStatusCode.Unauthorized.value,
            error = HttpStatusCode.Unauthorized.description
        )

    suspend fun rejectOffer(offer: Offer) =
        loginRepository.jwtToken()?.let { jwtToken ->
            getOfferReceived(offer.offerReceiverId, offer.offerReceiverProduct.id)?.run {
                deleteOfferApi.deleteOffer(jwtToken = jwtToken, offer.id).also {
                    it.deletedOfferId?.let { offerId ->
                        deleteOfferUpdateProduct(offerId, offer.offerReceiverProduct.id)
                    } ?: deleteOffer(offer.id)
                }
            } ?: DeleteOfferResponse(HttpStatusCode.OK.value)

        } ?: DeleteOfferResponse(
            statusCode = HttpStatusCode.Unauthorized.value,
            error = HttpStatusCode.Unauthorized.description
        )

    suspend fun acceptOffer(offer: Offer) = loginRepository.jwtToken()?.let { jwtToken ->
        getOfferReceived(offer.offerReceiverId, offer.offerReceiverProduct.id)?.run {
            if (accepted.not())
                acceptOfferApi.acceptOffer(jwtToken = jwtToken, offer.id).also {
                    it.acceptedOfferId?.let { acceptedOfferId ->
                        onlyTradeDB.transaction {
                            offerDao.accept(true, acceptedOfferId)
                            productDao.traded(true, offer.offerReceiverProduct.id)
                            offer.offeredProducts.forEach { offeredProduct ->
                                productDao.traded(true, offeredProduct.id)
                            }
                        }
                    } ?: deleteOffer(offer.id)
                } else AcceptOfferResponse(HttpStatusCode.Accepted.value)
        } ?: AcceptOfferResponse(HttpStatusCode.NotFound.value)

    } ?: AcceptOfferResponse(
        statusCode = HttpStatusCode.Unauthorized.value,
        error = HttpStatusCode.Unauthorized.description
    )

    suspend fun completeOffer(offer: Offer) = loginRepository.jwtToken()?.let { jwtToken ->
        getOfferReceived(offer.offerReceiverId, offer.offerReceiverProduct.id)?.run {
            if (completed.not())
                completeOfferApi.completeOffer(jwtToken, offer.id).also {
                    it.completedOfferId?.let { completedOfferId ->
                        offerDao.transaction { offerDao.complete(true, completedOfferId) }
                    } ?: deleteOffer(offer.id)

                } else CompleteOfferResponse(HttpStatusCode.Accepted.value)
        } ?: CompleteOfferResponse(HttpStatusCode.NotFound.value)

    } ?: CompleteOfferResponse(
        statusCode = HttpStatusCode.Unauthorized.value,
        error = HttpStatusCode.Unauthorized.description
    )


    private fun addOffers(offers: List<Offer>) = offerDao.transaction {
        offers.forEach { offer ->
            offer.run {
                offerDao.add(
                    id = id,
                    offerMakerId = offerMakerId,
                    offerReceiverId = offerReceiverId,
                    offerReceiverProductId = offer.offerReceiverProduct.id,
                    offerReceiverProduct = Json.encodeToString(offerReceiverProduct),
                    offeredProducts = Json.encodeToString(offeredProducts),
                    extraPrice = extraPrice,
                    accepted = accepted,
                    completed = completed
                )
            }
        }
    }


    private fun deleteOfferUpdateProduct(offerId: Long, offerReceiverProductId: Long) =
        onlyTradeDB.transaction {
            offerDao.deleteById(offerId)
            val product = productDao.getById(offerReceiverProductId).executeAsOne()
            val offers = product.offers?.run { Json.decodeFromString<List<Offer>>(this) }
                ?.filterNot { it.id == offerId }?.run { Json.encodeToString(this) }
            productDao.updateOffers(offers, offerReceiverProductId)
        }

    private fun deleteOffer(offerId: Long) = offerDao.transaction {
        offerDao.deleteById(offerId)
    }
}