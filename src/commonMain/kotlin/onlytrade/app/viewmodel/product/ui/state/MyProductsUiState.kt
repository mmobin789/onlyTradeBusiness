package onlytrade.app.viewmodel.product.ui.state

import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer

sealed class MyProductsUiState {
    data object Idle : MyProductsUiState()
    data object LoadingProducts : MyProductsUiState()
    data object ProductsNotFound : MyProductsUiState()
    data class GetProductsApiError(val error: String) : MyProductsUiState()
    data object SelectionActive : MyProductsUiState()
    data class OfferMade(val offer: Offer) : MyProductsUiState()
    data object MakingOffer : MyProductsUiState()
    data object OffersExceeded : MyProductsUiState()
    data class AddOfferApiError(val error: String) : MyProductsUiState()
    data object DeletingProduct : MyProductsUiState()
    data object ProductInTrade : MyProductsUiState()
    data object ProductDeleted : MyProductsUiState()
    data class DeleteProductApiError(val error: String) : MyProductsUiState()
}