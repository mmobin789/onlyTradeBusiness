package onlytrade.app.viewmodel.product.add.repository.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class AddProductResponse(val statusCode: Int? = null, val error: String? = null)