package onlytrade.app.viewmodel.product.ui.state

import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer

sealed class ProductDetailUiState {
    data object Idle : ProductDetailUiState()
    data object LoadingOfferMade : ProductDetailUiState()
    data object LoadingOfferReceived : ProductDetailUiState()
    data object GuestUser : ProductDetailUiState()
    data object OfferReceived : ProductDetailUiState()
    data object OfferNotReceived : ProductDetailUiState()
    data class OfferMade(val offer: Offer) : ProductDetailUiState()
    data object OfferNotMade : ProductDetailUiState()
    data object WithdrawingOffer : ProductDetailUiState()
    data object OfferRejected : ProductDetailUiState()
    data object OfferWithdrawn : ProductDetailUiState()
    data class OfferDeleteApiError(val error: String) : ProductDetailUiState()
}