package onlytrade.app.viewmodel.home.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.product.repository.ProductRepository
import onlytrade.app.viewmodel.product.repository.data.db.Product

class GetProductsUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(pageNo: Int, userId: Int? = null) = withContext(IODispatcher) {
        productRepository.getProducts(pageNo = pageNo, userId = userId).run {
            when (statusCode) {
                HttpStatusCode.OK.value -> Result.GetProducts(products = products!!) //guaranteed non-null products.
                HttpStatusCode.NotFound.value -> Result.GetProducts(products = emptyList()) // all products loaded.
                else -> Result.Error(
                    error = error ?: "Something went wrong."
                ) // something went wrong would be a rare unhandled/unexpected find.
            }
        }
    }

    sealed class Result {
        data class GetProducts(val products: List<Product>) : Result()
        data class Error(val error: String) : Result()
    }
}