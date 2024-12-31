package onlytrade.app.viewmodel.login

import androidx.lifecycle.ViewModel
import onlytrade.app.viewmodel.login.usecase.EmailLoginUseCase
import onlytrade.app.viewmodel.login.usecase.MobileLoginUseCase

class LoginViewModel(
    private val mobileLoginUseCase: MobileLoginUseCase,
    private val emailLoginUseCase: EmailLoginUseCase
) : ViewModel() {


    fun doMobileLogin(phoneNumber: String) = mobileLoginUseCase(phoneNumber)
}