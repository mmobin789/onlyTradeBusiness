package onlytrade.app.viewmodel.login.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import onlytrade.app.viewmodel.login.repository.LoginRepository

class EmailLoginUseCase(private val loginRepository: LoginRepository) {

    suspend operator fun invoke(email: String, pwd: String) = withContext(Dispatchers.Default) {
        loginRepository.loginWithEmail(email, pwd)?.run {
            Result.OK(this)
        } ?: Result.Error()
    }

    sealed class Result {
        data class OK(val result: String) : Result()
        data class Error(val error: String? = null) : Result()
    }

}