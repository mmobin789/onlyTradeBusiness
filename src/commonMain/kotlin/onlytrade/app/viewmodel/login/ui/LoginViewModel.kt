package onlytrade.app.viewmodel.login.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlytrade.app.viewmodel.login.ui.LoginUiState.BlankEmailInputError
import onlytrade.app.viewmodel.login.ui.LoginUiState.BlankFormError
import onlytrade.app.viewmodel.login.ui.LoginUiState.BlankMobileInputError
import onlytrade.app.viewmodel.login.ui.LoginUiState.BlankPwdInputError
import onlytrade.app.viewmodel.login.ui.LoginUiState.EmailFormatInputError
import onlytrade.app.viewmodel.login.ui.LoginUiState.Idle
import onlytrade.app.viewmodel.login.ui.LoginUiState.Loading
import onlytrade.app.viewmodel.login.ui.LoginUiState.MobileNoFormatInputError
import onlytrade.app.viewmodel.login.ui.LoginUiState.SmallPwdInputError
import onlytrade.app.viewmodel.login.usecase.EmailLoginUseCase
import onlytrade.app.viewmodel.login.usecase.MobileLoginUseCase

class LoginViewModel(
    private val mobileLoginUseCase: MobileLoginUseCase,
    private val emailLoginUseCase: EmailLoginUseCase
) : ViewModel() {

    var uiState: MutableStateFlow<LoginUiState> = MutableStateFlow(Idle)
        private set

    private val pakistaniMobileNoRegex = Regex("^((\\+92)?(92)?(0)?)(3)([0-9]{9})")

    /**
     * Email address pattern, same as android.
     */
    private val emailAddressRegex = Regex(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun idle() {
        uiState.value = Idle
    }

    private fun loading() {
        uiState.value = Loading
    }


    fun doMobileLogin(mobileNo: String, pwd: String) {
        viewModelScope.launch {
            uiState.value = if (mobileNo.isBlank() && pwd.isBlank()) {
                BlankFormError

            } else if (mobileNo.isBlank()) {
                BlankMobileInputError

            } else if (mobileNo.matches(pakistaniMobileNoRegex).not()) {
                MobileNoFormatInputError

            } else if (pwd.isBlank()) {
                BlankPwdInputError

            } else if (pwd.replace("\\s".toRegex(), "").length < 7) {
                SmallPwdInputError

            } else {
                loading()

                when (mobileLoginUseCase(mobileNo, pwd)) {
                    is MobileLoginUseCase.Result.OK -> {
                        LoginUiState.LoggedIn()
                    }

                    else -> LoginUiState.ApiError()
                }
            }
        }
    }


    fun doEmailLogin(email: String, pwd: String) {

        if (email.isBlank() && pwd.isBlank()) {
            uiState.value = BlankFormError
            return
        }
        email.ifBlank {
            uiState.value = BlankEmailInputError
            return
        }

        if (email.matches(emailAddressRegex).not()) {
            uiState.value = EmailFormatInputError
            return
        }

        pwd.ifBlank {
            uiState.value = BlankPwdInputError
            return
        }

        val validPwd = pwd.replace("\\s".toRegex(), "")

        if (validPwd.length < 7) {
            uiState.value = SmallPwdInputError
            return
        }

        loading()

        viewModelScope.launch {
            uiState.value = when (emailLoginUseCase(email, validPwd)) {
                is EmailLoginUseCase.Result.OK -> LoginUiState.LoggedIn()
                else -> LoginUiState.ApiError()
            }
        }
    }
}