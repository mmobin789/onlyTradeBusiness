package onlytrade.app.viewmodel.login.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import onlytrade.app.viewmodel.login.repository.LoginRepository

class MobileLoginUseCase(
    private val loginRepository: LoginRepository,
    private val coroutineScope: CoroutineScope
) {

    operator fun invoke(phoneNumber: String) = coroutineScope.launch { loginRepository.loginWithPhone(phoneNumber) }


}