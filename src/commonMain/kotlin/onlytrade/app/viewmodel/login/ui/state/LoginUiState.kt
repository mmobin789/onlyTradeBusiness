package onlytrade.app.viewmodel.login.ui.state

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data object BlankFormError : LoginUiState()
    data object BlankMobileInputError : LoginUiState()
    data object BlankEmailInputError : LoginUiState()
    data object EmailFormatInputError : LoginUiState()
    data object MobileNoFormatInputError : LoginUiState()
    data object BlankPwdInputError : LoginUiState()
    data object SmallPwdInputError : LoginUiState()
    data class LoggedIn(val isAdmin: Boolean) : LoginUiState()
    data class ApiError(val error: String) : LoginUiState()
}