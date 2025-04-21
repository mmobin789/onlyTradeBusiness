package onlytrade.app.viewmodel.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.viewmodel.profile.usecase.GetProfileUseCase

class ProfileViewModel (
    private val getProfileUseCase: GetProfileUseCase,
) : ViewModel() {

    var uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState.Idle)
        private set

    fun idle() {
        uiState.value = ProfileUiState.Idle
    }

    private fun loading() {
        uiState.value = ProfileUiState.Loading
    }

    fun getProfile() {
        loading()
        viewModelScope.launch {
            when (val result = getProfileUseCase()) {
                is GetProfileUseCase.Result.OK -> {
                    uiState.value = ProfileUiState.Success(
                        name = result.name,
                        email = result.email,
                        phone = result.phone
                    )
                }
                is GetProfileUseCase.Result.Error -> {
                    uiState.value = ProfileUiState.Error(error = result.error)
                }
            }
        }
    }

}


