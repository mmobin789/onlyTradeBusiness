package onlytrade.app.viewmodel.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.component.AppScope
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.profile.ui.ProfileUiState.Idle
import onlytrade.app.viewmodel.profile.ui.ProfileUiState.LoadingKycStatus
import onlytrade.app.viewmodel.profile.ui.ProfileUiState.LoggedOut
import onlytrade.app.viewmodel.profile.ui.ProfileUiState.UserDetailApiError
import onlytrade.app.viewmodel.profile.ui.ProfileUiState.VerifiedUser
import onlytrade.app.viewmodel.profile.usecase.GetUserDetailUseCase
import onlytrade.app.viewmodel.profile.usecase.LogoutUseCase

class ProfileViewModel(
    loginRepository: LoginRepository,
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val logoutUseCase: LogoutUseCase

) : ViewModel() {

    var uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(Idle)
        private set

    val user = loginRepository.user()!!

    init {
        getUserDetails()
    }

    private fun getUserDetails() {
        uiState.value = LoadingKycStatus
        viewModelScope.launch {
            when (val result = getUserDetailUseCase(user.id)) {
                is GetUserDetailUseCase.Result.Detail -> {

                    uiState.value = if (result.user.verified) VerifiedUser else Idle
                }

                is GetUserDetailUseCase.Result.Error -> UserDetailApiError(result.error)
            }
        }
    }

    fun idle() {
        uiState.value = Idle
    }

    fun logOut() {
        AppScope.launch {
            uiState.value = LoggedOut
            logoutUseCase()

        }
    }

}


