package onlytrade.app.viewmodel.product.offer.repository.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class AddOfferResponse(val statusCode: Int? = null, val error: String? = null)