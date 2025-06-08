package onlytrade.app.viewmodel.admin.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.login.repository.UserRepository

class VerifyUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long) =
        withContext(IODispatcher) {
            userRepository.verifyUser(userId)
                .run {
                    when (statusCode) {
                        HttpStatusCode.Accepted.value -> Result.VerifiedUser
                        HttpStatusCode.NotFound.value -> Result.UserNotFound
                        else -> Result.Error(
                            error = error ?: "Something went wrong."
                        ) // something went wrong would be a rare unhandled/unexpected find.
                    }
                }
        }

    sealed class Result {
        data object UserNotFound : Result()
        data object VerifiedUser : Result()
        data class Error(val error: String) : Result()
    }
}