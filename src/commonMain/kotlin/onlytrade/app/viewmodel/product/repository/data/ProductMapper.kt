package onlytrade.app.viewmodel.product.repository.data

import kotlinx.serialization.json.Json
import onlytrade.app.viewmodel.product.repository.data.db.Product

object ProductMapper {

    fun toProduct(localProduct: onlytrade.db.Product) =
        Product(
            id = localProduct.id,
            categoryId = localProduct.categoryId,
            subcategoryId = localProduct.subcategoryId,
            userId = localProduct.userId,
            name = localProduct.name,
            description = localProduct.description,
            estPrice = localProduct.estPrice,
            imageUrls = localProduct.imageUrls.split(","),
            traded = localProduct.traded,
            offers = localProduct.offers?.let { Json.decodeFromString(it) }
        )

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
}