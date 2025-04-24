package onlytrade.app.viewmodel.offer.repository.data.db

import kotlinx.serialization.Serializable

@Serializable
data class Offer(
    val id: Long,
    val userId: Long,
    val productId: Long,
    val price: Double,
    val approved: Boolean
)