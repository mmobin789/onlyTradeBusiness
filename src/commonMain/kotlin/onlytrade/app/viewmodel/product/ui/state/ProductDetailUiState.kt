package onlytrade.app.viewmodel.product.ui.state

import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer

sealed class ProductDetailUiState {
    data object Idle : ProductDetailUiState()
    data object GuestUser : ProductDetailUiState()
    data object CheckMyOffer : ProductDetailUiState()
    data class MyOfferPlaced(val offer: Offer) : ProductDetailUiState()
    data object MakingOffer : ProductDetailUiState()
    data object MakeOfferFail : ProductDetailUiState()
}