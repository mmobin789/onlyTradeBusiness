package onlytrade.app.viewmodel.product.add.repository.data.remote.model.request


data class AddProductRequest(
    val name: String,
    val desc: String,
    val price: Int,
    val images: List<ByteArray>
)