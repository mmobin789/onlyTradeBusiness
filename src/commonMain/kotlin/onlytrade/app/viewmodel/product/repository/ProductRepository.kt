package onlytrade.app.viewmodel.product.repository

import com.russhwolf.settings.Settings
import io.ktor.http.HttpStatusCode
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.until
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.product.offer.repository.OfferRepository
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
    private val offerRepository: OfferRepository,
    private val localPrefs: Settings,
    onlyTradeDB: OnlyTradeDB
) {
    private val productLastUpdatedAt = "PRODUCTS_LAST_UPDATED_AT"

    private val dao = onlyTradeDB.productQueries

    suspend fun addProduct(addProductRequest: AddProductRequest) =
        loginRepository.jwtToken()?.run {
            addProductApi.addProduct(addProductRequest, jwtToken = this)
        } ?: AddProductResponse(
            statusCode = HttpStatusCode.Unauthorized.value,
            error = HttpStatusCode.Unauthorized.description
        )

    fun getProduct(productId: Long) = dao.transactionWithResult {
        val localProduct = dao.getById(productId).executeAsOne()
        toProduct(localProduct)
    }


    suspend fun getProducts(
        pageNo: Int,
        pageSize: Int,
        userId: Long? = null
    ) = localPrefs.getStringOrNull(productLastUpdatedAt)?.run {
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
            userId.toLong(),
            pageSize.toLong(),
            offset
        )
        else
            dao.selectPaged(pageSize.toLong(), offset)

        return GetProductsResponse().run {
            val localProductList =
                dao.transactionWithResult { localProducts.executeAsList() }
            if (localProductList.isEmpty()) {
                getProductsApi(pageNo, pageSize, userId)
            } else {
                copy(
                    statusCode = HttpStatusCode.OK.value,
                    products = localProductList.map(::toProduct)
                )
            }

        }
    } ?: run {
        getProductsApi(pageNo, pageSize, userId)
    }


    private suspend fun getProductsApi(pageNo: Int, pageSize: Int, userId: Long? = null) =
        getProductsApi.getProducts(pageNo, pageSize, userId).also {
            val products = it.products
            if (products.isNullOrEmpty()) {
                deleteProducts()
            } else {
                localPrefs.putString(
                    productLastUpdatedAt,
                    Clock.System.now().toString()
                )
                products.forEach { product ->
                    addProduct(product)
                    product.offers?.forEach { offer ->
                        offerRepository.addOffer(offer)
                    }
                }
            }
        }

    /**
     * method to delete all products from local db.
     * Do not call this from main thread.
     * Blocking synchronous operation.
     */
    private fun deleteProducts() {
        dao.transaction {
            dao.deleteAll()
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
                imageUrls = imageUrls.joinToString(",")
            )
        }
    }

    /*    private fun Product.toLocalProduct() = let { product ->
            onlytrade.db.Product(
                id = product.id,
                categoryId = product.categoryId,
                subcategoryId = product.subcategoryId,
                userId = product.userId,
                name = product.name,
                description = product.description,
                estPrice = product.estPrice,
                imageUrls = product.imageUrls.joinToString(",")
            )
        }*/

    private fun toProduct(localProduct: onlytrade.db.Product) =
        Product(
            id = localProduct.id,
            categoryId = localProduct.categoryId,
            subcategoryId = localProduct.subcategoryId,
            userId = localProduct.userId,
            name = localProduct.name,
            description = localProduct.description,
            estPrice = localProduct.estPrice,
            imageUrls = localProduct.imageUrls.split(","),
            offers = offerRepository.getOffersByProductId(localProduct.id).ifEmpty { null }
        )
}