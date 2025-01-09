package onlytrade.app.viewmodel.login.repository.data.remote

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

class LoginWebApi(private val client: HttpClient) {
    suspend fun greeting(): String? {
        val response: HttpResponse? = try {
            client.get("https://ktor.io/docs/")
        } catch (e: Exception) {
            Napier.e {
                e.stackTraceToString()
            }
            null
        }
        return response?.bodyAsText()
    }
}