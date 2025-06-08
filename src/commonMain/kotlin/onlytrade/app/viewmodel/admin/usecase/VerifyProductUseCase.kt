package onlytrade.app.viewmodel.admin.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.product.repository.ProductRepository

class VerifyProductUseCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: Long) =
        withContext(IODispatcher) {
            productRepository.verifyProduct(productId)
                .run {
                    when (statusCode) {
                        HttpStatusCode.Accepted.value -> Result.ProductApproved
                        HttpStatusCode.NotFound.value -> Result.ProductNotFound
                        else -> Result.Error(
                            error = error ?: "Something went wrong."
                        ) // something went wrong would be a rare unhandled/unexpected find.
                    }
                }
        }

    sealed class Result {
        data object ProductNotFound : Result()
        data object ProductApproved : Result()
        data class Error(val error: String) : Result()
    }
}