package onlytrade.app.viewmodel.product.repository.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class DeleteProductResponse(
    val statusCode: Int? = null,
    val error: String? = null,
    val deletedProductId: Long? = null
)
