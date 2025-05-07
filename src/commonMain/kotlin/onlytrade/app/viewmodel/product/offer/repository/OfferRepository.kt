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
import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.AddOfferApi
import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.DeleteOfferApi
import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.GetOffersApi
import onlytrade.app.viewmodel.product.offer.repository.data.remote.request.AddOfferRequest
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.AddOfferResponse
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.DeleteOfferResponse
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.GetOffersResponse
import onlytrade.app.viewmodel.product.repository.data.ProductMapper.toProduct
import onlytrade.db.OnlyTradeDB

class OfferRepository(
    private val loginRepository: LoginRepository,
    private val addOfferApi: AddOfferApi,
    private val getOffersApi: GetOffersApi,
    private val deleteOfferApi: DeleteOfferApi,
    private val localPrefs: Settings,
    onlyTradeDB: OnlyTradeDB
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
            minutesDiff >= 2  // 2 minutes //todo need to update server sync time.

        if (updateRequired) {
            getOffersApi()
        } else {
            val localOffers = offerDao.transactionWithResult {
                offerDao.getOffers().executeAsList().map {
                    toOffer(
                        it,
                        getProductsByIds(Json.decodeFromString(it.offeredProductIds))
                    )
                }
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
                ?.run { toOffer(this, getProductsByIds(Json.decodeFromString(offeredProductIds))) }
        }

    /**
     * The returns the 1st offer received by the user.
     * This method is offline only.
     */
    fun getOfferReceived(offerReceiverId: Long, offerReceiverProductId: Long) =
        offerDao.transactionWithResult {
            offerDao.getOfferReceived(offerReceiverId, offerReceiverProductId).executeAsOneOrNull()
                ?.run { toOffer(this, getProductsByIds(Json.decodeFromString(offeredProductIds))) }
        }

    suspend fun addOffer(
        offerReceiverId: Long, offerReceiverProductId: Long, offeredProductIds: LinkedHashSet<Long>
    ) = loginRepository.jwtToken()?.run {
        val addOfferRequest = AddOfferRequest(
            offer = Offer(
                id = 0,
                offerMakerId = loginRepository.user()!!.id, // This is guaranteed.
                offerReceiverId = offerReceiverId,
                offerReceiverProductId = offerReceiverProductId,
                offeredProductIds = offeredProductIds,
                extraPrice = 0.0,
                accepted = false,
                completed = false
            )
        )
        addOfferApi.addOffer(addOfferRequest, jwtToken = this).also {
            it.offer?.run {
                addOffer(this)
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
                offerReceiverProductId = offerReceiverProductId,
                offeredProductIds = Json.encodeToString(offeredProductIds),
                extraPrice = extraPrice,
                accepted = accepted,
                completed = completed
            )
        }
    }

    suspend fun withdrawOffer(offerMakerId: Long, offerReceiverProductId: Long) =
        loginRepository.jwtToken()?.run {
            getOfferMade(offerMakerId, offerReceiverProductId)?.let { offer ->
                deleteOfferApi.deleteOffer(this, offer.id).also {
                    it.deletedOfferId?.let { offerId ->
                        deleteOffer(offerId)
                    }
                }
            }

        } ?: DeleteOfferResponse(
            statusCode = HttpStatusCode.Unauthorized.value,
            error = HttpStatusCode.Unauthorized.description
        )

    suspend fun rejectOffer(offerId: Long) =
        loginRepository.jwtToken()?.run {
            deleteOfferApi.deleteOffer(this, offerId).also {
                it.deletedOfferId?.let { offerId ->
                    deleteOffer(offerId)
                }
            }

        } ?: DeleteOfferResponse(
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
                    offerReceiverProductId = offerReceiverProductId,
                    offeredProductIds = Json.encodeToString(offeredProductIds),
                    extraPrice = extraPrice,
                    accepted = accepted,
                    completed = completed
                )
            }
        }
    }


    private fun getOffersByProductId(productId: Long): List<Offer> =
        //todo maybe add remote fetch as well (very rare case as products would come with offers always when fetched from remote.
        offerDao.getOffersByProductId(productId).executeAsList()
            .map { toOffer(it, getProductsByIds(Json.decodeFromString(it.offeredProductIds))) }


    private fun getProductsByIds(ids: Set<Long>) =
        productDao.getProductsByIds(ids).executeAsList()
            .map { toProduct(it, getOffersByProductId(it.id)) }

    private fun deleteOffer(offerId: Long) = offerDao.transaction {
        offerDao.deleteById(offerId)
    }


}