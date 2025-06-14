package onlytrade.app.viewmodel.login.ui.usecase

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext
import onlytrade.app.IODispatcher
import onlytrade.app.viewmodel.login.repository.UserRepository

class KycUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(docs: List<ByteArray>) = withContext(IODispatcher) {
        userRepository.uploadDocs(docs).run {
            if (statusCode == HttpStatusCode.Accepted.value) // docs processing for review.
                Result.DocsInReview
            else Result.Error(error = error ?: "Something went wrong.")
        }
    }

    sealed class Result {
        data object DocsInReview : Result()
        data class Error(val error: String) : Result()
    }
}