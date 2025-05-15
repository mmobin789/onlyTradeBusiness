package onlytrade.app.viewmodel.login.ui.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.login.repository.LoginRepository

class EmailLoginUseCase(private val loginRepository: LoginRepository) {

    suspend operator fun invoke(email: String, pwd: String) = withContext(IODispatcher) {
        loginRepository.loginWithEmail(email, pwd).run {
            if (statusCode == HttpStatusCode.OK.value)
                Result.OK
            else Result.Error(error = error ?: "Something went wrong.")
        }
    }

    sealed class Result {
        data object OK : Result()
        data class Error(val error: String) : Result()
    }

}