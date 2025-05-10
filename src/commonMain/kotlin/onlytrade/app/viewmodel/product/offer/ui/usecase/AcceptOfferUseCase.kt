package onlytrade.app.viewmodel.product.offer.ui.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.product.offer.repository.OfferRepository
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer

class AcceptOfferUseCase(
    private val offerRepository: OfferRepository
) {
    suspend operator fun invoke(offer: Offer) =
        withContext(IODispatcher) {
            offerRepository.acceptOffer(offer)
                .run {
                    when (statusCode) {
                        HttpStatusCode.Accepted.value -> Result.OfferAccepted
                        HttpStatusCode.NotFound.value -> Result.OfferNotFound
                        else -> Result.Error(
                            error = error ?: "Something went wrong."
                        ) // something went wrong would be a rare unhandled/unexpected find.
                    }
                }
        }

    sealed class Result {
        data object OfferNotFound : Result()
        data object OfferAccepted : Result()
        data class Error(val error: String) : Result()
    }
}