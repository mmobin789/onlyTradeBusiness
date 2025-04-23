package onlytrade.app.viewmodel.product.repository.data.remote.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AddProductRequest(
    val name: String,
    val description: String,
    val categoryId: Long,
    val subcategoryId: Long,
    val estPrice: Double,
    @Transient
    val productImages: List<ByteArray>? = null
)