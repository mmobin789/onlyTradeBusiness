package onlytrade.app.viewmodel.product.offer.repository.data.db

import kotlinx.serialization.Serializable

@Serializable
data class Offer(
    val id: Long,
    val offerMakerId: Long,
    val offerReceiverId: Long,
    val productIds: List<Long>,
    val price: Double,
    val approved: Boolean
)