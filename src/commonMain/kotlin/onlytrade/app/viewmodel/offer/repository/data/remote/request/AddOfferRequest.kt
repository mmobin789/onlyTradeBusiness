package onlytrade.app.viewmodel.offer.repository.data.remote.request

import kotlinx.serialization.Serializable
import onlytrade.app.viewmodel.offer.repository.data.db.Offer

@Serializable
data class AddOfferRequest(
    val offer: Offer
)
