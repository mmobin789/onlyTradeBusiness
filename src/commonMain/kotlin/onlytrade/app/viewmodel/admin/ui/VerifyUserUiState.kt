package onlytrade.app.viewmodel.admin.ui

sealed class VerifyUserUiState {
    data object Idle : VerifyUserUiState()
    data object VerifyingUser : VerifyUserUiState()
    data object UserVerified : VerifyUserUiState()
    data object UserNotFound : VerifyUserUiState()
    data class VerifyUserApiError(val error: String) : VerifyUserUiState()
}