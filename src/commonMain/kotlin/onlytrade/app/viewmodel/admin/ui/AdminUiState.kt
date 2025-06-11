package onlytrade.app.viewmodel.admin.ui

sealed class AdminUiState {
    data object Idle : AdminUiState()
    data object LoadingProducts : AdminUiState()
    data object ProductsNotFound : AdminUiState()
    data object VerifyingProduct : AdminUiState()
    data object ProductVerified : AdminUiState()
    data object ProductNotFound : AdminUiState()
    data class VerifyProductApiError(val error: String) : AdminUiState()
    data class GetApprovalProductsApiError(val error: String) : AdminUiState()
    data object LoadingUsers : AdminUiState()
    data object UsersNotFound : AdminUiState()
    data class GetApprovalUsersApiError(val error: String) : AdminUiState()
    data object LoggedOut : AdminUiState()
}