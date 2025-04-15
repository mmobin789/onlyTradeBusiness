package onlytrade.app.viewmodel.product.repository.data.db

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val subcategoryId: Int,
    val id: Int,
    val userId: Int,
    val name: String,
    val description: String,
    val estPrice: Double,
    val imageUrls: List<String>
)