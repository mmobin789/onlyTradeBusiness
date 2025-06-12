package onlytrade.app.viewmodel.product.repository

import androidx.compose.ui.util.fastForEach
import com.russhwolf.settings.Settings
import io.ktor.http.HttpStatusCode
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.until
import kotlinx.serialization.json.Json
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer
import onlytrade.app.viewmodel.product.repository.data.ProductMapper.toProduct
import onlytrade.app.viewmodel.product.repository.data.db.Product
import onlytrade.app.viewmodel.product.repository.data.remote.api.AddProductApi
import onlytrade.app.viewmodel.product.repository.data.remote.api.DeleteProductApi
import onlytrade.app.viewmodel.product.repository.data.remote.api.GetApprovalProductsApi
import onlytrade.app.viewmodel.product.repository.data.remote.api.GetProductsApi
import onlytrade.app.viewmodel.product.repository.data.remote.api.GetUserProductsApi
import onlytrade.app.viewmodel.product.repository.data.remote.api.VerifyProductApi
import onlytrade.app.viewmodel.product.repository.data.remote.request.AddProductRequest
import onlytrade.app.viewmodel.product.repository.data.remote.response.AddProductResponse
import onlytrade.app.viewmodel.product.repository.data.remote.response.DeleteProductResponse
import onlytrade.app.viewmodel.product.repository.data.remote.response.GetProductsResponse
import onlytrade.app.viewmodel.product.repository.data.remote.response.VerifyProductResponse
import onlytrade.db.OnlyTradeDB

class ProductRepository(
    private val loginRepository: LoginRepository,
    private val addProductApi: AddProductApi,
    private val getProductsApi: GetProductsApi,
    private val getUserProductsApi: GetUserProductsApi,
    private val deleteProductApi: DeleteProductApi,
    private val getApprovalProductsApi: GetApprovalProductsApi,
    private val verifyProductApi: VerifyProductApi,
    private val localPrefs: Settings,
    private val onlyTradeDB: OnlyTradeDB
) {
    private val productsLastUpdatedAt = "PRODUCTS_LAST_UPDATED_AT"
    private val myProductsLastUpdatedAt = "MY_PRODUCTS_LAST_UPDATED_AT"

    private val dao = onlyTradeDB.productQueries
    private val offerProductDao = onlyTradeDB.offerProductQueries
    private val offerDao = onlyTradeDB.offerQueries

    suspend fun addProduct(addProductRequest: AddProductRequest) =
        loginRepository.jwtToken()?.run {
            addProductApi.addProduct(addProductRequest, jwtToken = this)
        } ?: AddProductResponse(
            statusCode = HttpStatusCode.Unauthorized.value,
            error = HttpStatusCode.Unauthorized.description
        )

    suspend fun deleteProduct(id: Long) = loginRepository.jwtToken()?.let { jwtToken ->
        deleteProductApi.deleteProduct(jwtToken = jwtToken, id).also {
            it.deletedProductId?.let { productId ->
                deleteLocalProduct(productId)
            } ?: deleteLocalProduct(id)
        }
    } ?: DeleteProductResponse(
        statusCode = HttpStatusCode.Unauthorized.value,
        error = HttpStatusCode.Unauthorized.description
    )

    suspend fun getApprovalProducts() =
        loginRepository.jwtToken()?.let { jwtToken ->
            getApprovalProductsApi.getApprovalProducts(jwtToken)
        } ?: GetProductsResponse(statusCode = HttpStatusCode.Unauthorized.value)

    suspend fun verifyProduct(productId: Long) =
        loginRepository.jwtToken()?.let { jwtToken ->
            verifyProductApi.verifyProduct(jwtToken, productId)
        } ?: VerifyProductResponse(statusCode = HttpStatusCode.Unauthorized.value)


    suspend fun getProducts(
        pageNo: Int, pageSize: Int
    ) = localPrefs.getStringOrNull(productsLastUpdatedAt)?.run {
        val productUpdateDateTime = Instant.parse(this)
        val now = Clock.System.now()
        val minutesDiff = productUpdateDateTime.until(now, DateTimeUnit.MINUTE)
        val updateRequired = minutesDiff >= 1  // 1 minute //todo need to update server sync time.

        if (updateRequired) {
            getProductsApi(pageNo, pageSize)
        } else {
            val userId = loginRepository.user()?.id
            val offset = if (pageNo > 1) {    // 2..20..3..40..4..60
                ((pageSize * pageNo) - pageSize).toLong()
            } else 0

            val localProducts = userId?.let { dao.selectPagedExcept(it, pageSize.toLong(), offset) }
                ?: dao.selectPaged(pageSize.toLong(), offset)

            return GetProductsResponse().run {
                val localProductList = dao.transactionWithResult {
                    localProducts.executeAsList().map(::toProduct)
                }
                if (localProductList.isEmpty()) {
                    copy(statusCode = HttpStatusCode.NotFound.value)
                } else {
                    copy(
                        statusCode = HttpStatusCode.PartialContent.value,
                        products = localProductList
                    )
                }

            }
        }
    } ?: getProductsApi(pageNo, pageSize)


    private suspend fun getProductsApi(
        pageNo: Int,
        pageSize: Int,
        userId: Long? = null
    ): GetProductsResponse {
        deleteProductsAndOffers(pageNo, pageSize, userId)
        return getProductsApi.getProducts(loginRepository.jwtToken(), pageNo, pageSize)
            .also {
                val products = it.products

                if (!products.isNullOrEmpty()) {
                    addProductsAndOffers(products)
                }

                localPrefs.putString(
                    if (userId == null) productsLastUpdatedAt else myProductsLastUpdatedAt,
                    Clock.System.now().toString()
                )
            }
    }


    suspend fun getMyProducts(
        pageNo: Int, pageSize: Int, userId: Long
    ) = localPrefs.getStringOrNull(myProductsLastUpdatedAt)?.run {
        val productUpdateDateTime = Instant.parse(this)
        val now = Clock.System.now()
        val minutesDiff = productUpdateDateTime.until(now, DateTimeUnit.MINUTE)
        val updateRequired = minutesDiff >= 1  // 1 minute //todo need to update server sync time.

        if (updateRequired) {
            getUserProductsApi(pageNo, pageSize, userId)
        } else {
            val offset = if (pageNo > 1) {    // 2..20..3..40..4..60
                ((pageSize * pageNo) - pageSize).toLong()
            } else 0

            val localProducts = dao.selectUsersPaged(userId, pageSize.toLong(), offset)

            return GetProductsResponse().run {
                val localProductList = dao.transactionWithResult {
                    localProducts.executeAsList().map(::toProduct)
                }
                if (localProductList.isEmpty()) {
                    copy(statusCode = HttpStatusCode.NotFound.value)
                } else {
                    copy(
                        statusCode = HttpStatusCode.PartialContent.value,
                        products = localProductList
                    )
                }

            }
        }
    } ?: getUserProductsApi(pageNo, pageSize, userId)


    private suspend fun getUserProductsApi(
        pageNo: Int,
        pageSize: Int,
        userId: Long? = null
    ) = loginRepository.jwtToken()?.let { jwtToken ->
        deleteProductsAndOffers(pageNo, pageSize, userId)
        return getUserProductsApi.getUserProducts(jwtToken, pageNo, pageSize)
            .also {
                val products = it.products

                if (!products.isNullOrEmpty()) {
                    addProductsAndOffers(products)
                }

                localPrefs.putString(myProductsLastUpdatedAt, Clock.System.now().toString())
            }
    } ?: GetProductsResponse(statusCode = HttpStatusCode.Unauthorized.value)


    /**
     * method to delete pages of products and all offers from local db.
     * Do not call this from main thread.
     * Blocking synchronous operation.
     */
    private fun deleteProductsAndOffers(pageNo: Int, pageSize: Int, userId: Long? = null) =
        onlyTradeDB.transaction {

            val offset = if (pageNo > 1) {    // 2..20..3..40..4..60
                ((pageSize * pageNo) - pageSize).toLong()
            } else 0
            if (userId != null)
                dao.deleteUserPaged(userId, pageSize.toLong(), offset)
            else
                dao.deletePaged(pageSize.toLong(), offset)

            offerDao.deleteAll()
        }


    /**
     * method to batch insert products with offers into local db.
     * DO NOT call this from main thread.
     */
    private fun addProductsAndOffers(products: List<Product>) {
        dao.transaction {
            products.fastForEach { product ->
                product.run {
                    dao.insert(
                        id = id,
                        categoryId = categoryId,
                        subcategoryId = subcategoryId,
                        userId = userId,
                        name = name,
                        description = description,
                        estPrice = estPrice,
                        imageUrls = imageUrls.joinToString(","),
                        traded = traded,
                        offers = offers?.let { Json.encodeToString(it) })

                    offers?.let { offers ->
                        addOffers(offers)
                    }
                }
            }
        }
    }

    private fun addOffers(offers: List<Offer>) = offers.forEach { offer ->
        offer.run {
            offerDao.add(
                id = id,
                offerMakerId = offerMakerId,
                offerReceiverId = offerReceiverId,
                offerReceiverProductId = offerReceiverProduct.id,
                offerReceiverProduct = Json.encodeToString(offerReceiverProduct),
                extraPrice = extraPrice,
                accepted = accepted,
                completed = completed
            )

            offeredProducts.fastForEach {
                offerProductDao.add(it.id, id)
            }
        }
    }

    private fun deleteLocalProduct(id: Long) = dao.transaction { dao.deleteById(id) }

    /**
     * method to insert product into local db.
     * DO NOT call this from main thread.
     */
    /*   private fun addProduct(product: Product) = dao.transaction {
           product.run {
               dao.insert(
                   id = id,
                   categoryId = categoryId,
                   subcategoryId = subcategoryId,
                   userId = userId,
                   name = name,
                   description = description,
                   estPrice = estPrice,
                   imageUrls = imageUrls.joinToString(","),
                   traded = traded,
                   offers = offers?.let { Json.encodeToString(it) }
               )
           }
       }*/

}