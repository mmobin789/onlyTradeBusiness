package onlytrade.app.viewmodel.product.repository.data.db

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Long,
    val categoryId: Long,
    val subcategoryId: Long,
    val userId: Long,
    val name: String,
    val description: String,
    val estPrice: Double,
    val imageUrls: List<String>
)