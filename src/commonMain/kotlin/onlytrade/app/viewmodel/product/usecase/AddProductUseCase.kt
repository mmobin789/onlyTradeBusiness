package onlytrade.app.viewmodel.product.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.product.add.repository.data.remote.request.AddProductRequest
import onlytrade.app.viewmodel.product.repository.ProductRepository

class AddProductUseCase(private val productRepository: ProductRepository) {

    suspend operator fun invoke(
        name: String,
        description: String,
        subcategoryId: Long,
        estPrice: Double,
        productImages: List<ByteArray>
    ) =
        withContext(IODispatcher) {
            val addProductRequest = AddProductRequest(
                name = name,
                description = description,
                subcategoryId = subcategoryId,
                estPrice = estPrice,
                productImages = productImages

            )
            productRepository.addProduct(addProductRequest = addProductRequest).run {
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