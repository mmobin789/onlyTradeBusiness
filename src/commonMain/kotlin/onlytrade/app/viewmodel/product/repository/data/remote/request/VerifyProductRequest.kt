package onlytrade.app.viewmodel.product.repository.data.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class VerifyProductRequest(val productId: Long)