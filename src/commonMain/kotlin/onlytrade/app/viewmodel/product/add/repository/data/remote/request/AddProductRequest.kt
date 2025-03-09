package onlytrade.app.viewmodel.product.add.repository.data.remote.request


data class AddProductRequest(
    val name: String,
    val subcategoryId: Int,
    val description: String,
    val estPrice: Double
)