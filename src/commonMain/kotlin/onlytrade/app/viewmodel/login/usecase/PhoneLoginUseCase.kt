package onlytrade.app.viewmodel.login.usecase

import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.LoginResponse

class PhoneLoginUseCase(
    private val loginRepository: LoginRepository
) {

    suspend operator fun invoke(mobileNo: String, pwd: String) = withContext(IODispatcher) {
        loginRepository.loginWithPhone(mobileNo, pwd)?.run {
            Result.OK(this)
        } ?: Result.Error()
    }

    sealed class Result {
        data class OK(val result: LoginResponse) : Result()
        data class Error(val error: String? = null) : Result()
    }
}

