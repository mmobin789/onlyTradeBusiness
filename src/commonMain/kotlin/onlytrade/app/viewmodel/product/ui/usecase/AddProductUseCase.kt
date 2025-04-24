package onlytrade.app.viewmodel.product.ui.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.repository.ProductRepository
import onlytrade.app.viewmodel.product.repository.data.db.Product
import onlytrade.app.viewmodel.product.repository.data.remote.request.AddProductRequest

class AddProductUseCase(
    private val loginRepository: LoginRepository,
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(
        name: String,
        description: String,
        categoryId: Long,
        subcategoryId: Long,
        estPrice: Double,
        productImages: List<ByteArray>
    ) =
        withContext(IODispatcher) {
            val addProductRequest = AddProductRequest(
                Product(
                    id = 0,
                    userId = loginRepository.user()?.id ?: 0,
                    name = name,
                    description = description,
                    categoryId = categoryId,
                    subcategoryId = subcategoryId,
                    estPrice = estPrice,
                    imageUrls = emptyList()
                ),
                productImages = productImages

            )
            productRepository.addProduct(addProductRequest).run {
                if (statusCode == HttpStatusCode.Created.value) // product processing for review.
                    Result.OK
                else Result.Error(error = error ?: "Something went wrong.")
            }
        }

    sealed class Result {
        data object OK : Result()
        data class Error(val error: String) : Result()
    }

}