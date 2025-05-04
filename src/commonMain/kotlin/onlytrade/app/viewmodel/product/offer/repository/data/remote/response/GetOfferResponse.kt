package onlytrade.app.viewmodel.product.offer.repository.data.remote.response

import kotlinx.serialization.Serializable
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer

@Serializable
data class GetOfferResponse(
    val statusCode: Int? = null,
    val error: String? = null,
    val offer: Offer? = null
)