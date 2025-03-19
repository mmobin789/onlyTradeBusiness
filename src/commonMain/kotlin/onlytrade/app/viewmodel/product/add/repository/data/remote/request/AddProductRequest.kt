package onlytrade.app.viewmodel.product.add.repository.data.remote.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AddProductRequest(
    val name: String,
    val description: String,
    val subcategoryId: Int,
    val estPrice: Double,
    @Transient
    val productImages: List<ByteArray>? = null
)