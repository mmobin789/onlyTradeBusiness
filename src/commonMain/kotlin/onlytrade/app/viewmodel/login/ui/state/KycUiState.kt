package onlytrade.app.viewmodel.login.ui.state

sealed class KycUiState {
    data object Idle : KycUiState()
    data object BlankMobileInputError : KycUiState()
    data object BlankEmailInputError : KycUiState()
    data object EmailFormatInputError : KycUiState()
    data object MobileNoFormatInputError : KycUiState()
    data object DocsIncomplete : KycUiState()
    data object Uploading : KycUiState()
    data object InReview : KycUiState()
    data class KycApiError(val error: String) : KycUiState()
}