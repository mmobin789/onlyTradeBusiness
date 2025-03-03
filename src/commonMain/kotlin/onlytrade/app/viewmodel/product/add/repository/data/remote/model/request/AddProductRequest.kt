package onlytrade.app.viewmodel.product.add.repository.data.remote.model.request


import java.io.File

data class AddProductRequest(
    val name: String,
    val desc: String,
    val price: Int,
    val images: List<File>
)