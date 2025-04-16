package onlytrade.app.viewmodel.splash

import androidx.lifecycle.ViewModel
import onlytrade.app.viewmodel.login.repository.LoginRepository

class SplashViewModel(private val loginRepository: LoginRepository) : ViewModel() {
    fun isUserLoggedIn() = loginRepository.isUserLoggedIn()
}