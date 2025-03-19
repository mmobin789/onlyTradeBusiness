package onlytrade.app.viewmodel.login.repository.data.remote

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.basicAuth
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class LoginApi(private val client: HttpClient) { //todo

    suspend fun loginByPhone(phone: String, pwd: String): String? {
        val response: HttpResponse? = try {
            client.post("https://onlytrade.co/login/phone") {
                contentType(ContentType.Application.Json)
                basicAuth(phone, pwd)
            }
        } catch (e: Exception) {
            Napier.e {
                e.stackTraceToString()
            }
            null
        }
        return response?.bodyAsText()
    }

    suspend fun loginByEmail(email: String, pwd: String): String? {
        val response: HttpResponse? = try {
            client.post("https://onlytrade.co/login/email") {
                contentType(ContentType.Application.Json)
                basicAuth(email, pwd)
            }
        } catch (e: Exception) {
            Napier.e {
                e.stackTraceToString()
            }
            null
        }
        return response?.bodyAsText()
    }
}