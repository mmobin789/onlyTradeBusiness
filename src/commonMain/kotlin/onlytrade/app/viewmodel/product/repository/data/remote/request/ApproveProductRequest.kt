package onlytrade.app.viewmodel.product.repository.data.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class ApproveProductRequest(val productId: Long)