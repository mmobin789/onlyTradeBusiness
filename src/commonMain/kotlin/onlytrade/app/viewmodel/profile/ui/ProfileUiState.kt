package onlytrade.app.viewmodel.profile.ui

sealed class ProfileUiState {
    data object Idle : ProfileUiState()
    data object Loading : ProfileUiState()
    data object BlankNameError : ProfileUiState()
    data object InvalidPhoneFormatError : ProfileUiState()
    data object InvalidEmailFormatError : ProfileUiState()
    data object LoggedOut : ProfileUiState()
    data object Updated : ProfileUiState()

    data class Success(
        val name: String,
        val phone: String?,
        val email: String?
    ) : ProfileUiState()

    data class Error(val error: String) : ProfileUiState()
}