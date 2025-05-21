package onlytrade.app.viewmodel.profile.ui

sealed class ProfileUiState {
    data object Idle : ProfileUiState()
    data object BlankNameError : ProfileUiState()
    data object InvalidPhoneFormatError : ProfileUiState()
    data object InvalidEmailFormatError : ProfileUiState()
    data object LoggedOut : ProfileUiState()
    data object VerifiedUser : ProfileUiState()
    data object LoadingKycStatus : ProfileUiState()
    data class UserDetailApiError(val error: String) : ProfileUiState()
}