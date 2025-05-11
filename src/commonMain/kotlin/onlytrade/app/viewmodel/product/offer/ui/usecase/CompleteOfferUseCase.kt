package onlytrade.app.viewmodel.product.offer.ui.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.product.offer.repository.OfferRepository
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer

class CompleteOfferUseCase(
    private val offerRepository: OfferRepository
) {
    suspend operator fun invoke(offer: Offer) =
        withContext(IODispatcher) {
            offerRepository.completeOffer(offer)
                .run {
                    when (statusCode) {
                        HttpStatusCode.Accepted.value -> Result.OfferCompleted
                        else -> Result.Error(
                            error = error ?: "Something went wrong."
                        ) // something went wrong would be a rare unhandled/unexpected find.
                    }
                }
        }

    sealed class Result {
        data object OfferCompleted : Result()
        data class Error(val error: String) : Result()
    }
}