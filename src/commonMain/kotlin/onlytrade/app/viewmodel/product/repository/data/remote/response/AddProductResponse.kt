package onlytrade.app.viewmodel.product.repository.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class AddProductResponse(val statusCode: Int? = null, val error: String? = null)