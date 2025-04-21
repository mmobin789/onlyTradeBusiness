package onlytrade.app.viewmodel.profile.usecase
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.login.repository.LoginRepository

class GetProfileUseCase(private val loginRepository: LoginRepository) {

    suspend operator fun invoke(): Result = withContext(IODispatcher) {
        val user = loginRepository.user()
        if (user != null) {
            Result.OK(name = user.name ?: "", email = user.email, phone = user.phone)
        } else Result.Error("User not logged in")
    }

    sealed class Result {
        data class OK(val name: String, val email: String?, val phone: String?) : Result()
        data class Error(val error: String) : Result()
    }
}
