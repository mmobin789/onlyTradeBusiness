package onlytrade.app.viewmodel.product.offer.repository.data

import kotlinx.serialization.json.Json
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer
import onlytrade.app.viewmodel.product.repository.data.db.Product

object OfferMapper {
    fun toOffer(offer: onlytrade.db.Offer, offeredProducts: List<Product>) = offer.run {
        Offer(
            id = id,
            offerMakerId = offerMakerId,
            offerReceiverId = offerReceiverId,
            offerReceiverProductId = offerReceiverProductId,
            offeredProductIds = Json.decodeFromString(offeredProductIds),
            extraPrice = extraPrice,
            accepted = accepted,
            completed = completed,
            offeredProducts = offeredProducts
        )
    }
}