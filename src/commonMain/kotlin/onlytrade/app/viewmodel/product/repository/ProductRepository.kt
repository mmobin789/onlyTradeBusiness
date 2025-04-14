package onlytrade.app.viewmodel.product.repository

import onlytrade.app.viewmodel.product.add.repository.data.remote.AddProductApi
import onlytrade.app.viewmodel.product.add.repository.data.remote.request.AddProductRequest

class ProductRepository(private val addProductApi: AddProductApi) {

    suspend fun addProduct(addProductRequest: AddProductRequest) =
        addProductApi.addProduct(addProductRequest)
}