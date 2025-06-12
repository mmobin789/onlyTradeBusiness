package onlytrade.app.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.viewmodel.admin.ui.VerifyUserUiState
import onlytrade.app.viewmodel.admin.ui.VerifyUserUiState.Idle
import onlytrade.app.viewmodel.admin.ui.VerifyUserUiState.UserNotFound
import onlytrade.app.viewmodel.admin.ui.VerifyUserUiState.UserVerified
import onlytrade.app.viewmodel.admin.ui.VerifyUserUiState.VerifyUserApiError
import onlytrade.app.viewmodel.admin.ui.VerifyUserUiState.VerifyingUser
import onlytrade.app.viewmodel.admin.usecase.VerifyUserUseCase

class VerifyUserViewModel(
    private val verifyUserUseCase: VerifyUserUseCase
) : ViewModel() {
    var uiState: MutableStateFlow<VerifyUserUiState> = MutableStateFlow(Idle)
        private set

    fun idle() {
        uiState.value = Idle
    }

    fun verifyUser(userId: Long) {
        uiState.value = VerifyingUser

        viewModelScope.launch {
            uiState.value = when (val result = verifyUserUseCase(userId)) {
                VerifyUserUseCase.Result.VerifiedUser -> UserVerified.also {
                    AdminNav.emit(AdminNav.Event.RefreshAdminScreen)
                }
                VerifyUserUseCase.Result.UserNotFound -> UserNotFound
                is VerifyUserUseCase.Result.Error -> VerifyUserApiError(error = result.error)
            }
        }
    }

}