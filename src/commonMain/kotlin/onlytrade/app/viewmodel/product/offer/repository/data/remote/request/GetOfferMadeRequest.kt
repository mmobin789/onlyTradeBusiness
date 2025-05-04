package onlytrade.app.viewmodel.product.offer.repository.data.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class GetOfferMadeRequest(val offerMakerId: Long, val productId: Long)
