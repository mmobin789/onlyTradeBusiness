package onlytrade.app.viewmodel.product.repository.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class ApproveProductResponse(
    val statusCode: Int? = null,
    val error: String? = null,
    val productId: Long? = null
)