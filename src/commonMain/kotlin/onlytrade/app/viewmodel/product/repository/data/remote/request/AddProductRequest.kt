package onlytrade.app.viewmodel.product.repository.data.remote.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import onlytrade.app.viewmodel.product.repository.data.db.Product

@Serializable
data class AddProductRequest(
    val product: Product,
    @Transient
    val productImages: List<ByteArray>? = null
)