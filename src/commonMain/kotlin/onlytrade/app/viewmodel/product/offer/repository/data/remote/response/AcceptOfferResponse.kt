package onlytrade.app.viewmodel.product.offer.repository.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class AcceptOfferResponse(
    val statusCode: Int? = null,
    val error: String? = null,
    val acceptedOfferId: Long? = null
)