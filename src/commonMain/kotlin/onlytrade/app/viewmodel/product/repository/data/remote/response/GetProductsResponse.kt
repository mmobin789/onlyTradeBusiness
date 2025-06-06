package onlytrade.app.viewmodel.product.repository.data.remote.response

import kotlinx.serialization.Serializable
import onlytrade.app.viewmodel.product.repository.data.db.Product

@Serializable
data class GetProductsResponse(
    val statusCode: Int? = null,
    val pageSize: Int? = null,
    val pageNo: Int? = null,
    val products: List<Product>? = null,
    val error: String? = null
)