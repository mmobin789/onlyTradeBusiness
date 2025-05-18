package onlytrade.app.viewmodel.login.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.component.AppScope
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.login.ui.LoginViewModel.Companion.emailAddressRegex
import onlytrade.app.viewmodel.login.ui.LoginViewModel.Companion.pakistaniMobileNoRegex
import onlytrade.app.viewmodel.login.ui.state.KycUiState
import onlytrade.app.viewmodel.login.ui.state.KycUiState.BlankEmailInputError
import onlytrade.app.viewmodel.login.ui.state.KycUiState.BlankMobileInputError
import onlytrade.app.viewmodel.login.ui.state.KycUiState.DocsIncomplete
import onlytrade.app.viewmodel.login.ui.state.KycUiState.EmailFormatInputError
import onlytrade.app.viewmodel.login.ui.state.KycUiState.Idle
import onlytrade.app.viewmodel.login.ui.state.KycUiState.InReview
import onlytrade.app.viewmodel.login.ui.state.KycUiState.KycApiError
import onlytrade.app.viewmodel.login.ui.state.KycUiState.MobileNoFormatInputError
import onlytrade.app.viewmodel.login.ui.state.KycUiState.Uploading
import onlytrade.app.viewmodel.login.ui.usecase.KycUseCase


class KycViewModel(
    private val loginRepository: LoginRepository,
    private val kycUseCase: KycUseCase
) : ViewModel() {
    var uiState: MutableStateFlow<KycUiState> = MutableStateFlow(Idle)
        private set

    fun idle() {
        uiState.value = Idle
    }

    fun isEmailProvided() = loginRepository.user()?.email.isNullOrBlank().not()

    fun uploadDocs(
        docs: List<ByteArray>,
        email: String?,
        mobileNo: String?
    ) {
        val user = loginRepository.user()

        uiState.value = if (user?.email.isNullOrBlank()) {
            if (email.isNullOrBlank()) {
                BlankEmailInputError
                return
            } else if (email.matches(emailAddressRegex).not()) {
                EmailFormatInputError
                return
            } else Idle

        } else {
            if (mobileNo.isNullOrBlank()) {
                BlankMobileInputError
                return
            } else if (mobileNo.matches(pakistaniMobileNoRegex).not()) {
                MobileNoFormatInputError
                return
            } else Idle
        }

        if (docs.isEmpty() || docs.size < 3) {
            uiState.value = DocsIncomplete
            return
        }

        uiState.value = Uploading


        AppScope.launch {
            uiState.value = when (val result = kycUseCase(docs)) {
                KycUseCase.Result.DocsInReview -> InReview
                is KycUseCase.Result.Error -> KycApiError(error = result.error)
            }
        }
    }

}