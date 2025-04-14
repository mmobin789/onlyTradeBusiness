package onlytrade.app.viewmodel.product.repository.data.remote.response

import kotlinx.serialization.Serializable
import onlytrade.app.viewmodel.product.repository.data.db.Product

@Serializable
data class GetProductResponse(val statusCode: Int, val products: List<Product>?)