package onlytrade.app.viewmodel.product.offer.repository.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class CompleteOfferResponse(
    val statusCode: Int? = null,
    val error: String? = null,
    val completedOfferId: Long? = null
)