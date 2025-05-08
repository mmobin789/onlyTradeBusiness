package onlytrade.app.viewmodel.product.offer.repository.data.db

import kotlinx.serialization.Serializable
import onlytrade.app.viewmodel.product.repository.data.db.Product

@Serializable
data class Offer(
    val id: Long,
    val offerMakerId: Long,
    val offerReceiverId: Long,
    val offerReceiverProductId: Long,
    val offeredProductIds: Set<Long>,
    val extraPrice: Double,
    val accepted: Boolean,
    val completed: Boolean,
    val offeredProducts: List<Product>
)