package onlytrade.app.viewmodel.product.offer.repository.data

import kotlinx.serialization.json.Json
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer
import onlytrade.app.viewmodel.product.repository.data.ProductMapper
import onlytrade.db.OnlyTradeDB
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


object OfferMapper : KoinComponent {
    private val onlyTradeDB: OnlyTradeDB by inject()
    private val productDao = onlyTradeDB.productQueries

    fun toOffer(offer: onlytrade.db.Offer) = offer.run {
        val offeredProductIds = Json.decodeFromString<Set<Long>>(offeredProductIds)
        Offer(
            id = id,
            offerMakerId = offerMakerId,
            offerReceiverId = offerReceiverId,
            offerReceiverProductId = offerReceiverProductId,
            offeredProductIds = offeredProductIds,
            offerReceiverProduct = Json.decodeFromString(offerReceiverProduct),
            extraPrice = extraPrice,
            accepted = accepted,
            completed = completed,
            offeredProducts = getProductsByIds(offeredProductIds)
        )
    }

    private fun getProductsByIds(ids: Set<Long>) =
        productDao.getProductsByIds(ids).executeAsList()
            .map { ProductMapper.toProduct(it) }
}