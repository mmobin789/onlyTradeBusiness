package onlytrade.app.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.viewmodel.admin.ui.UserDetailUiState
import onlytrade.app.viewmodel.admin.ui.UserDetailUiState.Idle
import onlytrade.app.viewmodel.admin.ui.UserDetailUiState.UserNotFound
import onlytrade.app.viewmodel.admin.ui.UserDetailUiState.UserVerified
import onlytrade.app.viewmodel.admin.ui.UserDetailUiState.VerifyUserApiError
import onlytrade.app.viewmodel.admin.ui.UserDetailUiState.VerifyingUser
import onlytrade.app.viewmodel.admin.usecase.VerifyUserUseCase

class UserDetailViewModel(
    private val verifyUserUseCase: VerifyUserUseCase
) : ViewModel() {
    var uiState: MutableStateFlow<UserDetailUiState> = MutableStateFlow(Idle)
        private set

    fun idle() {
        uiState.value = Idle
    }

    fun verifyUser(userId: Long) {
        uiState.value = VerifyingUser

        viewModelScope.launch {
            uiState.value = when (val result = verifyUserUseCase(userId)) {
                VerifyUserUseCase.Result.VerifiedUser -> UserVerified
                VerifyUserUseCase.Result.UserNotFound -> UserNotFound
                is VerifyUserUseCase.Result.Error -> VerifyUserApiError(error = result.error)
            }
        }
    }

}