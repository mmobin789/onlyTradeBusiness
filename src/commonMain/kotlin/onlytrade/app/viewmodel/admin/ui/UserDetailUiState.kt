package onlytrade.app.viewmodel.admin.ui

sealed class UserDetailUiState {
    data object Idle : UserDetailUiState()
    data object VerifyingUser : UserDetailUiState()
    data object UserVerified : UserDetailUiState()
    data object UserNotFound : UserDetailUiState()
    data class VerifyUserApiError(val error: String) : UserDetailUiState()
}