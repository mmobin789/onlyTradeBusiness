package onlytrade.app.viewmodel.product.offer.repository.data

import kotlinx.serialization.json.Json
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer
import onlytrade.app.viewmodel.product.repository.data.ProductMapper
import onlytrade.db.OnlyTradeDB
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


object OfferMapper : KoinComponent {

    private val db by inject<OnlyTradeDB>()
    private val offerProductDao = db.offerProductQueries
    private val productDao = db.productQueries

    fun toOffer(offer: onlytrade.db.Offer) = offer.run {
        val offeredProductIds =
            offerProductDao.getOfferedProducts(offer.id).executeAsList().map { it.offeredProductId }

        val offeredProducts = productDao.getProducts(offeredProductIds).executeAsList().map {
            ProductMapper.toProduct(it)
        }

        Offer(
            id = id,
            offerMakerId = offerMakerId,
            offerReceiverId = offerReceiverId,
            offerReceiverProduct = Json.decodeFromString(offerReceiverProduct),
            extraPrice = extraPrice,
            accepted = accepted,
            completed = completed,
            offeredProducts = offeredProducts
        )
    }
}