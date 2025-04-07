package onlytrade.app.viewmodel.product.usecase

import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.product.add.repository.data.remote.request.AddProductRequest
import onlytrade.app.viewmodel.product.repository.ProductRepository

class AddProductUseCase(private val productRepository: ProductRepository) {

    suspend operator fun invoke(
        name: String,
        description: String,
        subcategoryId: Int,
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
            productRepository.addProduct(addProductRequest = addProductRequest)?.run {
                if (statusCode == 201) // product processing for review.
                    Result.OK(result = msg)
                else Result.Error(error = msg)
            } ?: Result.Error()
        }

    sealed class Result {
        data class OK(val result: String) : Result()
        data class Error(val error: String? = null) : Result()
    }

}