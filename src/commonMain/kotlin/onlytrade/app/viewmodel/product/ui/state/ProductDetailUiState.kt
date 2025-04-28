package onlytrade.app.viewmodel.product.ui.state

import onlytrade.app.viewmodel.product.repository.data.db.Product

sealed class ProductDetailUiState {
    data object Idle : ProductDetailUiState()
    data object LoadingDetail : ProductDetailUiState()
    data object MakingOffer : ProductDetailUiState()
    data object OfferMade : ProductDetailUiState()
    data object MakeOfferFail : ProductDetailUiState()
    data class ProductFound(val product: Product) : ProductDetailUiState()
}