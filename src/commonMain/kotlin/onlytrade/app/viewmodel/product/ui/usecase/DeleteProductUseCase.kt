package onlytrade.app.viewmodel.product.ui.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.product.repository.ProductRepository

class DeleteProductUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(productId: Long) = withContext(IODispatcher) {
        productRepository.deleteProduct(productId).run {
            when (statusCode) {
                HttpStatusCode.OK.value -> Result.ProductDeleted
                HttpStatusCode.Forbidden.value -> Result.ProductInTrade
                else -> Result.Error(error ?: "Something went wrong.")
            }

        }
    }

    sealed class Result {
        data object ProductDeleted : Result()
        data object ProductInTrade : Result()
        data class Error(val error: String) : Result()
    }
}