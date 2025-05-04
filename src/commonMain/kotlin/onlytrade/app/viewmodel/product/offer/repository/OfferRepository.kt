package onlytrade.app.viewmodel.product.offer.repository

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer
import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.AddOfferApi
import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.GetOfferMadeApi
import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.GetOfferReceivedApi
import onlytrade.app.viewmodel.product.offer.repository.data.remote.request.AddOfferRequest
import onlytrade.app.viewmodel.product.offer.repository.data.remote.request.GetOfferMadeRequest
import onlytrade.app.viewmodel.product.offer.repository.data.remote.request.GetOfferReceivedRequest
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.AddOfferResponse
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.GetOfferResponse
import onlytrade.db.OnlyTradeDB

class OfferRepository(
    private val loginRepository: LoginRepository,
    private val addOfferApi: AddOfferApi,
    private val getOfferMadeApi: GetOfferMadeApi,
    private val getOfferReceivedApi: GetOfferReceivedApi,
    onlyTradeDB: OnlyTradeDB
) {
    private val offerDao = onlyTradeDB.offerQueries

    suspend fun getOfferMade(offerMakerId: Long, offerReceiverProductId: Long) =
        offerDao.transactionWithResult {
            offerDao.getOfferMade(offerMakerId, offerReceiverProductId).executeAsOneOrNull()
                ?.run { toModel(this) }
        }.let { localOffer ->
            if (localOffer == null) {
                loginRepository.jwtToken()?.run {
                    getOfferMadeApi.getOfferMade(
                        GetOfferMadeRequest(
                            offerMakerId, offerReceiverProductId
                        ), jwtToken = this
                    ).also {
                        it.offer?.run {
                            addOffer(this)
                        }
                    }
                } ?: run {
                    GetOfferResponse(
                        statusCode = HttpStatusCode.Unauthorized.value,
                        error = HttpStatusCode.Unauthorized.description
                    )
                }
            } else GetOfferResponse(offer = localOffer)
        }


    suspend fun getOfferReceived(offerReceiverId: Long, offerReceiverProductId: Long) =
        offerDao.transactionWithResult {
            offerDao.getOfferReceived(offerReceiverId, offerReceiverProductId).executeAsOneOrNull()
                ?.run { toModel(this) }
        }.let { localOffer ->
            if (localOffer == null) {
                loginRepository.jwtToken()?.run {
                    getOfferReceivedApi.getOfferReceived(
                        GetOfferReceivedRequest(
                            offerReceiverId, offerReceiverProductId
                        ), jwtToken = this
                    ).also {
                        it.offer?.run {
                            addOffer(this)
                        }
                    }
                } ?: run {
                    GetOfferResponse(
                        statusCode = HttpStatusCode.Unauthorized.value,
                        error = HttpStatusCode.Unauthorized.description
                    )
                }
            } else GetOfferResponse(offer = localOffer)
        }

    suspend fun addOffer(
        offerReceiverId: Long, offerReceiverProductId: Long, offeredProductIds: HashSet<Long>
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

    fun addOffer(offer: Offer) = offerDao.transaction {
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

    fun getOffersByProductId(productId: Long) = //todo add fetch from remote.
        offerDao.getOffersByProductId(productId).executeAsList().map(::toModel)


    private fun toModel(offer: onlytrade.db.Offer) = offer.run {
        Offer(
            id = id,
            offerMakerId = offerMakerId,
            offerReceiverId = offerReceiverId,
            offerReceiverProductId = offerReceiverProductId,
            offeredProductIds = Json.decodeFromString(offeredProductIds),
            extraPrice = extraPrice,
            accepted = accepted,
            completed = completed
        )
    }

    /*    private fun Offer.toLocalOffer() = run {
            onlytrade.db.Offer(
                id = id,
                offerMakerId = offerMakerId,
                offerReceiverId = offerReceiverId,
                offerReceiverProductId = offerReceiverProductId,
                offeredProductIds = Json.encodeToString(offeredProductIds),
                extraPrice = extraPrice,
                accepted = accepted,
                completed = completed
            )
        }*/
}