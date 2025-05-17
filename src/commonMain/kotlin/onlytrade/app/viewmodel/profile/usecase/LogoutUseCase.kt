package onlytrade.app.viewmodel.profile.usecase

import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.login.repository.LoginRepository

class LogoutUseCase(private val loginRepository: LoginRepository) {
    suspend operator fun invoke() = withContext(IODispatcher) {
        loginRepository.logOut()
    }
}