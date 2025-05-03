package onlytrade.app.viewmodel.product.offer.repository

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer
import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.AddOfferApi
import onlytrade.app.viewmodel.product.offer.repository.data.remote.request.AddOfferRequest
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.AddOfferResponse
import onlytrade.db.OnlyTradeDB

class OfferRepository(
    private val loginRepository: LoginRepository,
    private val addOfferApi: AddOfferApi,
    onlyTradeDB: OnlyTradeDB
) {
    private val offerDao = onlyTradeDB.offerQueries

    fun getMyOffer(offerMakerId: Long, offerReceiverProductId: Long) =
        offerDao.transactionWithResult {
            offerDao.getMyOffer(offerMakerId, offerReceiverProductId).executeAsOneOrNull()
                ?.run { toModel(this) }
        }

    suspend fun addOffer(
        offerReceiverId: Long,
        offerReceiverProductId: Long,
        offeredProductIds: HashSet<Long>
    ) =
        loginRepository.jwtToken()?.run {
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

    fun getOffersByProductId(productId: Long) =
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