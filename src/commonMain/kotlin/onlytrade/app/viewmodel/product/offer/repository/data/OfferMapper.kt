package onlytrade.app.viewmodel.product.offer.repository.data

import kotlinx.serialization.json.Json
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer
import org.koin.core.component.KoinComponent


object OfferMapper : KoinComponent {

    fun toOffer(offer: onlytrade.db.Offer) = offer.run {
        Offer(
            id = id,
            offerMakerId = offerMakerId,
            offerReceiverId = offerReceiverId,
            offerReceiverProduct = Json.decodeFromString(offerReceiverProduct),
            extraPrice = extraPrice,
            accepted = accepted,
            completed = completed,
            offeredProducts = Json.decodeFromString(offeredProducts)
        )
    }
}