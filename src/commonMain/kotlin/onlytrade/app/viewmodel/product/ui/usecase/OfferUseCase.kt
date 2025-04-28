package onlytrade.app.viewmodel.product.ui.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.product.offer.repository.OfferRepository

class OfferUseCase(private val offerRepository: OfferRepository) {
    suspend operator fun invoke(offerReceiverId: Long, productIds: List<Long>) =
        withContext(IODispatcher) {
            offerRepository.addOffer(offerReceiverId, productIds).run {
                if (statusCode == HttpStatusCode.Created.value) // offer made.
                    Result.OfferMade
                else Result.Error(error = error ?: "Something went wrong.")
            }
        }

    sealed class Result {
        data object OfferMade : Result()
        data class Error(val error: String) : Result()
    }

}