package onlytrade.app.viewmodel.splash

import androidx.lifecycle.ViewModel
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.login.repository.data.db.UserType.ADMIN

class SplashViewModel(private val loginRepository: LoginRepository) : ViewModel() {
    fun isUserLoggedIn() = loginRepository.isUserLoggedIn()

    fun isAdmin() = loginRepository.user()?.userType == ADMIN
}