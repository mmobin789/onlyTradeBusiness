package onlytrade.app.viewmodel.product.offer.ui.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.product.offer.repository.OfferRepository

class WithdrawOfferUseCase(
    private val offerRepository: OfferRepository
) {
    suspend operator fun invoke(offerMakerId: Long, offerReceiverProductId: Long) =
        withContext(IODispatcher) {
            offerRepository.withdrawOffer(offerMakerId, offerReceiverProductId)
                .run {
                    when (statusCode) {
                        HttpStatusCode.OK.value -> Result.OfferDeleted
                        HttpStatusCode.NotFound.value -> Result.OfferNotFound
                        else -> Result.Error(
                            error = error ?: "Something went wrong."
                        ) // something went wrong would be a rare unhandled/unexpected find.
                    }
                }
        }

    sealed class Result {
        data object OfferNotFound : Result()
        data object OfferDeleted : Result()
        data class Error(val error: String) : Result()
    }
}