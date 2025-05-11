package onlytrade.app.viewmodel.product.offer.repository.data.remote.response

import kotlinx.serialization.Serializable
import onlytrade.app.viewmodel.login.repository.data.db.User

@Serializable
data class AcceptOfferResponse(
    val statusCode: Int? = null,
    val error: String? = null,
    val acceptedOfferId: Long? = null,
    val offerMaker: User? = null
)