package onlytrade.app.viewmodel.product.repository

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
import onlytrade.app.viewmodel.product.repository.data.remote.api.GetProductsApi
import onlytrade.app.viewmodel.product.repository.data.remote.request.AddProductRequest
import onlytrade.app.viewmodel.product.repository.data.remote.response.AddProductResponse
import onlytrade.app.viewmodel.product.repository.data.remote.response.GetProductsResponse
import onlytrade.db.OnlyTradeDB

class ProductRepository(
    private val loginRepository: LoginRepository,
    private val addProductApi: AddProductApi,
    private val getProductsApi: GetProductsApi,
    private val localPrefs: Settings,
    onlyTradeDB: OnlyTradeDB
) {
    private val productsLastUpdatedAt = "PRODUCTS_LAST_UPDATED_AT"

    private val dao = onlyTradeDB.productQueries

    private val offerDao = onlyTradeDB.offerQueries

    suspend fun addProduct(addProductRequest: AddProductRequest) =
        loginRepository.jwtToken()?.run {
            addProductApi.addProduct(addProductRequest, jwtToken = this)
        } ?: AddProductResponse(
            statusCode = HttpStatusCode.Unauthorized.value,
            error = HttpStatusCode.Unauthorized.description
        )

    suspend fun getProducts(
        pageNo: Int, pageSize: Int, userId: Long? = null
    ) = localPrefs.getStringOrNull(productsLastUpdatedAt)?.run {
        val productUpdateDateTime = Instant.parse(this)
        val now = Clock.System.now()
        val minutesDiff = productUpdateDateTime.until(now, DateTimeUnit.MINUTE)
        val updateRequired = minutesDiff >= 2  // 2 minutes //todo need to update server sync time.

        if (updateRequired) {
            getProductsApi(pageNo, pageSize, userId)
        }

        val offset = if (pageNo > 1) {    // 2..20..3..40..4..60
            ((pageSize * pageNo) - pageSize).toLong()
        } else 0

        val localProducts = if (userId != null) dao.selectUsersPaged(
            userId.toLong(), pageSize.toLong(), offset
        )
        else dao.selectPaged(pageSize.toLong(), offset)

        return GetProductsResponse().run {
            val localProductList = dao.transactionWithResult {
                localProducts.executeAsList().map(::toProduct)
            }
            if (localProductList.isEmpty()) {
                getProductsApi(pageNo, pageSize, userId)
            } else {
                copy(
                    statusCode = HttpStatusCode.OK.value, products = localProductList
                )
            }

        }
    } ?: getProductsApi(pageNo, pageSize, userId)


    private suspend fun getProductsApi(pageNo: Int, pageSize: Int, userId: Long? = null) =
        getProductsApi.getProducts(pageNo, pageSize, userId).also {
            val products = it.products
            if (products.isNullOrEmpty()) {
                deleteProductsAndOffers()
            } else {
                localPrefs.putString(
                    productsLastUpdatedAt, Clock.System.now().toString()
                )
                addProductsAndOffers(products)
            }
        }

    /**
     * method to delete all products and offers from local db.
     * Do not call this from main thread.
     * Blocking synchronous operation.
     */
    private fun deleteProductsAndOffers() {
        dao.transaction {
            dao.deleteAll()
            offerDao.deleteAll()
        }
    }

    /**
     * method to batch insert products with offers into local db.
     * DO NOT call this from main thread.
     */
    private fun addProductsAndOffers(products: List<Product>) {
        dao.transaction {
            products.forEach { product ->
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
                offerReceiverProductId = offerReceiverProductId,
                offerReceiverProduct = Json.encodeToString(offerReceiverProduct),
                offeredProductIds = Json.encodeToString(offeredProductIds),
                extraPrice = extraPrice,
                accepted = accepted,
                completed = completed
            )
        }
    }

    /**
     * method to insert product into local db.
     * DO NOT call this from main thread.
     */
    private fun addProduct(product: Product) = dao.transaction {
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
    }

}