package onlytrade.app.viewmodel.product.repository

import io.ktor.http.HttpStatusCode
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.add.repository.AddProductApi
import onlytrade.app.viewmodel.product.add.repository.data.remote.request.AddProductRequest
import onlytrade.app.viewmodel.product.add.repository.data.remote.response.AddProductResponse
import onlytrade.app.viewmodel.product.repository.data.db.Product
import onlytrade.app.viewmodel.product.repository.data.remote.response.GetProductsResponse
import onlytrade.db.OnlyTradeDB

class ProductRepository(
    private val addProductApi: AddProductApi,
    private val getProductsApi: GetProductsApi,
    private val loginRepository: LoginRepository,
    private val onlyTradeDB: OnlyTradeDB
) {


    suspend fun addProduct(addProductRequest: AddProductRequest) =
        loginRepository.jwtToken()?.run {
            addProductApi.addProduct(addProductRequest, jwtToken = this)
        } ?: AddProductResponse(
            statusCode = HttpStatusCode.Unauthorized.value,
            error = HttpStatusCode.Unauthorized.description
        )

    suspend fun getProducts(
        pageNo: Int,
        pageSize: Int,
        userId: Int? = null
    ): GetProductsResponse {

        val productDao = onlyTradeDB.productQueries

        val offset = if (pageNo > 1) {    // 2..20..3..40..4..60
            ((pageSize * pageNo) - pageSize).toLong()
        } else 0

        val localProducts = if (userId != null) productDao.selectUsersPaged(
            userId.toLong(),
            pageSize.toLong(),
            offset
        )
        else
            productDao.selectPaged(pageSize.toLong(), offset)

        return GetProductsResponse().run {
            val localProductList = localProducts.executeAsList()
            if (localProductList.isEmpty()) {
                getProductsApi.getProducts(pageNo, pageSize, userId).also {
                    it.products?.forEach { product ->
                        addProduct(product)
                    }
                }


            } else {
                copy(
                    statusCode = HttpStatusCode.OK.value,
                    products = localProductList.map(::toProduct)
                )
            }

        }


    }

    /**
     * method to insert product into local db.
     * DO NOT call this from main thread.
     */
    private fun addProduct(product: Product) {
        val productDao = onlyTradeDB.productQueries
        val localProduct = product.toLocalProduct()
        onlyTradeDB.transaction {
            productDao.insert(
                id = localProduct.id,
                subcategoryId = localProduct.subcategoryId,
                userId = localProduct.userId,
                name = localProduct.name,
                description = localProduct.description,
                estPrice = localProduct.estPrice,
                imageUrls = localProduct.imageUrls
            )
        }
    }

    private fun Product.toLocalProduct() = let { product ->
        onlytrade.db.Product(
            id = product.id,
            subcategoryId = product.subcategoryId,
            userId = product.userId,
            name = product.name,
            description = product.description,
            estPrice = product.estPrice,
            imageUrls = product.imageUrls.joinToString(",")
        )
    }

    private fun toProduct(localProduct: onlytrade.db.Product) =
        Product(
            id = localProduct.id,
            subcategoryId = localProduct.subcategoryId,
            userId = localProduct.userId,
            name = localProduct.name,
            description = localProduct.description,
            estPrice = localProduct.estPrice,
            imageUrls = localProduct.imageUrls.split(",")
        )
}