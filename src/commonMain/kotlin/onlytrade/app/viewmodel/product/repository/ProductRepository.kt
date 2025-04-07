package onlytrade.app.viewmodel.product.repository

import onlytrade.app.viewmodel.product.add.repository.data.remote.request.AddProductRequest
import onlytrade.app.viewmodel.product.repository.data.remote.ProductApi

class ProductRepository(private val productApi: ProductApi) {

    suspend fun addProduct(addProductRequest: AddProductRequest) =
        productApi.addProduct(addProductRequest)
}