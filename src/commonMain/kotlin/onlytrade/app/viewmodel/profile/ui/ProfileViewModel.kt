package onlytrade.app.viewmodel.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.viewmodel.profile.ui.ProfileUiState.Idle
import onlytrade.app.viewmodel.profile.ui.ProfileUiState.Loading
import onlytrade.app.viewmodel.profile.ui.ProfileUiState.Success
import onlytrade.app.viewmodel.profile.ui.ProfileUiState.Error
import onlytrade.app.viewmodel.profile.ui.ProfileUiState.LoggedOut
import onlytrade.app.viewmodel.profile.usecase.GetProfileUseCase
import onlytrade.app.viewmodel.profile.usecase.LogoutUseCase
import onlytrade.app.viewmodel.login.repository.LoginRepository

class ProfileViewModel (
    private val getProfileUseCase: GetProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    loginRepository: LoginRepository

) : ViewModel() {

    val isUserLoggedIn = loginRepository.isUserLoggedIn()
    var uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(Idle)
        private set

    fun idle() {
        uiState.value = Idle
    }

    private fun loading() {
        uiState.value = Loading
    }

    fun getProfile() {
        loading()
        viewModelScope.launch {
            when (val result = getProfileUseCase()) {
                is GetProfileUseCase.Result.OK -> {
                    uiState.value = Success(
                        name = result.name,
                        email = result.email,
                        phone = result.phone
                    )
                }
                is GetProfileUseCase.Result.Error -> {
                    uiState.value = Error(error = result.error)
                }
            }
        }
    }

    fun logout() {
        loading()
        viewModelScope.launch {
            logoutUseCase()
            uiState.value = LoggedOut
        }
    }

}


