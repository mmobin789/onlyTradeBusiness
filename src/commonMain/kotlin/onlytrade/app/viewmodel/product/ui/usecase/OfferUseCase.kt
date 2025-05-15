package onlytrade.app.viewmodel.product.ui.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.product.offer.repository.OfferRepository
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer

class OfferUseCase(private val offerRepository: OfferRepository) {
    suspend operator fun invoke(
        offerReceiverId: Long, offerReceiverProductId: Long, offeredProductIds: LinkedHashSet<Long>
    ) = withContext(IODispatcher) {
        offerRepository.addOffer(offerReceiverId, offerReceiverProductId, offeredProductIds)
            .run {
                when (statusCode) {
                    HttpStatusCode.Created.value // offer made.
                        -> Result.OfferMade(offer!!) // offer guaranteed.
                    HttpStatusCode.NotAcceptable.value -> Result.OffersExceeded
                    else -> Result.Error(error = error ?: "Something went wrong.")
                }
            }
    }

    sealed class Result {
        data class OfferMade(val offer: Offer) : Result()
        data object OffersExceeded : Result()
        data class Error(val error: String) : Result()
    }

}