package onlytrade.app.viewmodel.product.add.repository.data.db

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val userId: Int,
    val name: String,
    val description: String,
    val estPrice: Double
)