package onlytrade.app.viewmodel.product.offer.repository.data.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class AddOfferRequest(
    val offeredProductIds: Set<Long>,
    val offerMakerId: Long,
    val offerReceiverId: Long,
    val offerReceiverProductId: Long
)
