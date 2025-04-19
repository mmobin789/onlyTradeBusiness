package onlytrade.app.viewmodel.home.ui

sealed class HomeUiState {
    data object Idle : HomeUiState()
    data object LoadingProducts : HomeUiState()
    data object ProductsNotFound : HomeUiState()
    data class GetProductsApiError(val error: String) : HomeUiState()
}