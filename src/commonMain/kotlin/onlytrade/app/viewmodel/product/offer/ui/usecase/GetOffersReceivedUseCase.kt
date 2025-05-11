package onlytrade.app.viewmodel.product.offer.ui.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.offer.repository.OfferRepository
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer

class GetOffersReceivedUseCase(
    private val loginRepository: LoginRepository,
    private val offerRepository: OfferRepository
) {
    suspend operator fun invoke() =
        withContext(IODispatcher) {
            offerRepository.getOffers()
                .run {
                    when (statusCode) {
                        HttpStatusCode.OK.value -> {
                            val offerList = offers!!.filter {
                                it.offerReceiverId == loginRepository.user()?.id
                            }
                            if (offerList.isEmpty())
                                Result.OffersNotFound
                            else
                                Result.Offers(offers = offerList) //guaranteed non-null products.
                        }

                        HttpStatusCode.NotFound.value -> Result.OffersNotFound // all products loaded or no products at all.
                        else -> Result.Error(
                            error = error ?: "Something went wrong."
                        ) // something went wrong would be a rare unhandled/unexpected find.
                    }
                }
        }

    sealed class Result {
        data object OffersNotFound : Result()
        data class Offers(val offers: List<Offer>) : Result()
        data class Error(val error: String) : Result()
    }
}