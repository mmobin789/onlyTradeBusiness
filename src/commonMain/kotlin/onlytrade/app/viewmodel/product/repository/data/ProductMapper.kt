package onlytrade.app.viewmodel.product.repository.data

import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer
import onlytrade.app.viewmodel.product.repository.data.db.Product

object ProductMapper {

    fun toProduct(localProduct: onlytrade.db.Product, offersByProductId: List<Offer>) =
        Product(
            id = localProduct.id,
            categoryId = localProduct.categoryId,
            subcategoryId = localProduct.subcategoryId,
            userId = localProduct.userId,
            name = localProduct.name,
            description = localProduct.description,
            estPrice = localProduct.estPrice,
            imageUrls = localProduct.imageUrls.split(","),
            offers = offersByProductId.ifEmpty { null }
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