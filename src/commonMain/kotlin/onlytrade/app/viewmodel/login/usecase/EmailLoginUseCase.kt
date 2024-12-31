package onlytrade.app.viewmodel.login.usecase

import onlytrade.app.viewmodel.login.repository.LoginRepository

class EmailLoginUseCase(private val loginRepository: LoginRepository) {

    operator fun invoke(email:String) = loginRepository.loginWithEmail(email)


}