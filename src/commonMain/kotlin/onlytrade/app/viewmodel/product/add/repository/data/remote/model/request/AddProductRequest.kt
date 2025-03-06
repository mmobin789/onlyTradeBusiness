package onlytrade.app.viewmodel.product.add.repository.data.remote.model.request


data class AddProductRequest(
    val name: String,
    val category: String,
    val description: String,
    val estPrice: Double
)