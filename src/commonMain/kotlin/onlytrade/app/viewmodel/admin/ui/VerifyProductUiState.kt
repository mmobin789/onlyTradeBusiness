package onlytrade.app.viewmodel.admin.ui

sealed class VerifyProductUiState {
    data object Idle : VerifyProductUiState()
    data object VerifyingProduct : VerifyProductUiState()
    data object ProductVerified : VerifyProductUiState()
    data object ProductNotFound : VerifyProductUiState()
    data class VerifyProductApiError(val error: String) : VerifyProductUiState()
}