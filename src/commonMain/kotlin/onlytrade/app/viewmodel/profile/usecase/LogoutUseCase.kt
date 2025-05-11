package onlytrade.app.viewmodel.profile.usecase
import onlytrade.app.viewmodel.login.repository.LoginRepository

class LogoutUseCase (private val loginRepository: LoginRepository) {
    operator fun invoke() {
        loginRepository.logout()
    }
}