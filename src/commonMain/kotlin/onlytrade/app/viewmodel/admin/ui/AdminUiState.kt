package onlytrade.app.viewmodel.admin.ui

sealed class AdminUiState {
    data object Idle : AdminUiState()
    data object LoadingProducts : AdminUiState()
    data object ProductsNotFound : AdminUiState()
    data class GetApprovalProductsApiError(val error: String) : AdminUiState()
    data object LoadingUsers : AdminUiState()
    data object UsersNotFound : AdminUiState()
    data class GetApprovalUsersApiError(val error: String) : AdminUiState()
    data object LoggedOut : AdminUiState()
}