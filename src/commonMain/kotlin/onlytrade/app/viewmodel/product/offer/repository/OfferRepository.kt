package onlytrade.app.viewmodel.product.offer.repository

import io.ktor.http.HttpStatusCode
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer
import onlytrade.app.viewmodel.product.offer.repository.data.remote.api.AddOfferApi
import onlytrade.app.viewmodel.product.offer.repository.data.remote.request.AddOfferRequest
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.AddOfferResponse
import onlytrade.db.OnlyTradeDB

class OfferRepository(
    private val loginRepository: LoginRepository,
    private val addOfferApi: AddOfferApi,
    private val onlyTradeDB: OnlyTradeDB //todo when impl getOffers.
) {

    suspend fun addOffer(offerReceiverId: Long, productIds: HashSet<Long>) =
        loginRepository.jwtToken()?.run {
            val addOfferRequest = AddOfferRequest(
                offer = Offer(
                    id = 0,
                    offerMakerId = loginRepository.user()!!.id, // This is guaranteed.
                    offerReceiverId = offerReceiverId,
                    productIds = productIds,
                    price = 0.0,
                    approved = false
                )
            )
            addOfferApi.addOffer(addOfferRequest, jwtToken = this)
        } ?: AddOfferResponse(
            statusCode = HttpStatusCode.Unauthorized.value,
            error = HttpStatusCode.Unauthorized.description
        )
}