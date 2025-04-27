package onlytrade.app.viewmodel.product.ui.state

import onlytrade.app.viewmodel.product.repository.data.db.Product

sealed class ProductDetailUiState {
    data object Idle : ProductDetailUiState()
    data object LoadingDetail : ProductDetailUiState()
    data class ProductFound(val product: Product) : ProductDetailUiState()
}