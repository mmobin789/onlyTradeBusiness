package onlytrade.app.viewmodel.product.offer.repository.data.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class CompleteOfferRequest(
    val offerId: Long
)