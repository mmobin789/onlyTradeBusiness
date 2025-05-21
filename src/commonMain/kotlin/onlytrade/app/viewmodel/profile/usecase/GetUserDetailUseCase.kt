package onlytrade.app.viewmodel.profile.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.login.repository.UserRepository
import onlytrade.app.viewmodel.login.repository.data.db.User

class GetUserDetailUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(userId: Long): Result = withContext(IODispatcher) {
        userRepository.getUserDetail(userId).run {
            if (statusCode == HttpStatusCode.OK.value) Result.Detail(user!!)
            else Result.Error(error ?: "Something is wrong")
        }
    }

    sealed class Result {
        data class Detail(val user: User) : Result()
        data class Error(val error: String) : Result()
    }
}
