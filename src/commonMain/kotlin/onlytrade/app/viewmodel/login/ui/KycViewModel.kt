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
import onlytrade.app.viewmodel.login.ui.state.KycUiState.BlankNameError
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
        name: String,
        photoId: ByteArray?,
        photo: ByteArray?,
        email: String?,
        mobileNo: String?
    ) {
        val user = loginRepository.user()

        if (user?.email.isNullOrBlank()) {
            if (email.isNullOrBlank()) {
                uiState.value = BlankEmailInputError
                return
            }
            if (email.matches(emailAddressRegex).not()) {
                uiState.value = EmailFormatInputError
                return
            }

        } else {
            if (mobileNo.isNullOrBlank()) {
                uiState.value = BlankMobileInputError
                return
            }
            if (mobileNo.matches(pakistaniMobileNoRegex).not()) {
                uiState.value = MobileNoFormatInputError
                return
            }
        }
        if (name.isBlank()) {
            uiState.value = BlankNameError
            return
        }

        if (photoId == null || photo == null) {
            uiState.value = DocsIncomplete
            return
        }

        uiState.value = Uploading


        AppScope.launch {
            uiState.value = when (val result = kycUseCase(name, photoId, photo)) {
                KycUseCase.Result.DocsInReview -> InReview
                is KycUseCase.Result.Error -> KycApiError(error = result.error)
            }
        }
    }

}