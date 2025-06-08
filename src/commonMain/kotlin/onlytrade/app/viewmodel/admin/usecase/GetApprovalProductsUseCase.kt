package onlytrade.app.viewmodel.admin.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.product.repository.ProductRepository
import onlytrade.app.viewmodel.product.repository.data.db.Product

class GetApprovalProductsUseCase(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke() =
        withContext(IODispatcher) {
            productRepository.getApprovalProducts()
                .run {
                    when (statusCode) {
                        HttpStatusCode.OK.value -> Result.ApprovalProducts(products = products!!) //guaranteed non-null products.
                        HttpStatusCode.NotFound.value -> Result.ProductsNotFound // all products loaded or no products at all.
                        else -> Result.Error(
                            error = error ?: "Something went wrong."
                        ) // something went wrong would be a rare unhandled/unexpected find.
                    }
                }
        }

    sealed class Result {
        data object ProductsNotFound : Result()
        data class ApprovalProducts(val products: List<Product>) : Result()
        data class Error(val error: String) : Result()
    }
}