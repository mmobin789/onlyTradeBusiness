package onlytrade.app.viewmodel.product.ui

sealed class MyProductsUiState {
    data object Idle : MyProductsUiState()
    data object LoadingProducts : MyProductsUiState()
    data object ProductsNotFound : MyProductsUiState()
    data class GetProductsApiError(val error: String) : MyProductsUiState()
}