package onlytrade.app.viewmodel.home.ui

import onlytrade.app.viewmodel.product.repository.data.db.Product

sealed class HomeUiState {
    data object Idle : HomeUiState()
    data object LoadingProducts : HomeUiState()
    data class ProductList(val products: List<Product>) : HomeUiState()
    data class GetProductsApiError(val error: String) : HomeUiState()
}