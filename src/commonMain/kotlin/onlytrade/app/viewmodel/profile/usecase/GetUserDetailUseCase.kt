package onlytrade.app.viewmodel.profile.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.login.repository.LoginRepository
import onlytrade.app.viewmodel.login.repository.UserRepository
import onlytrade.app.viewmodel.login.repository.data.db.User

class GetUserDetailUseCase(
    private val loginRepository: LoginRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): Result = withContext(IODispatcher) {
        loginRepository.user()?.let { currentUser ->
            userRepository.getUserDetail(currentUser.id).run {
                if (statusCode == HttpStatusCode.OK.value && user != null) Result.Detail(user)
                else Result.Error(error ?: "Something is wrong")
            }
        } ?: Result.Error("User not logged in")

    }

    sealed class Result {
        data class Detail(val user: User) : Result()
        data class Error(val error: String) : Result()
    }
}
