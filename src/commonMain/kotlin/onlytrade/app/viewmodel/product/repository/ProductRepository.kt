package onlytrade.app.viewmodel.product.repository

import io.ktor.http.HttpStatusCode
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.add.repository.AddProductApi
import onlytrade.app.viewmodel.product.add.repository.data.remote.request.AddProductRequest
import onlytrade.app.viewmodel.product.add.repository.data.remote.response.AddProductResponse

class ProductRepository(
    private val addProductApi: AddProductApi,
    private val getProductsApi: GetProductsApi,
    private val loginRepository: LoginRepository
) {

    suspend fun addProduct(addProductRequest: AddProductRequest) =
        loginRepository.getJwtToken()?.run {
        addProductApi.addProduct(addProductRequest, jwtToken = this)
    } ?: AddProductResponse(
        statusCode = HttpStatusCode.Unauthorized.value,
        error = HttpStatusCode.Unauthorized.description
    )

    suspend fun getProducts(pageNo: Int, pageSize: Int = 20, userId: Int? = null) =
        getProductsApi.getProducts(pageNo, pageSize, userId)
}