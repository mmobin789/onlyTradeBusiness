package onlytrade.app.viewmodel.product.ui.state

sealed class ProductDetailUiState {
    data object Idle : ProductDetailUiState()
    data object MakingOffer : ProductDetailUiState()
    data object OfferMade : ProductDetailUiState()
    data object MakeOfferFail : ProductDetailUiState()
}