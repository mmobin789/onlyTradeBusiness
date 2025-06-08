package onlytrade.app.viewmodel.admin.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.login.repository.UserRepository
import onlytrade.app.viewmodel.login.repository.data.db.User

class GetApprovalUsersUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() =
        withContext(IODispatcher) {
            userRepository.getApprovalUsers()
                .run {
                    when (statusCode) {
                        HttpStatusCode.OK.value -> Result.ApprovalUsers(users!!) //guaranteed non-null users.
                        HttpStatusCode.NotFound.value -> Result.UsersNotFound // no users needing approval.
                        else -> Result.Error(
                            error = error ?: "Something went wrong."
                        ) // something went wrong would be a rare unhandled/unexpected find.
                    }
                }
        }

    sealed class Result {
        data object UsersNotFound : Result()
        data class ApprovalUsers(val users: List<User>) : Result()
        data class Error(val error: String) : Result()
    }
}