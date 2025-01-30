package onlytrade.app.viewmodel.login.repository.data.remote

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class LoginApiClient(private val client: HttpClient) { //todo
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

    suspend fun loginByPhone(phone: String, pwd: String): String? {
        val response: HttpResponse? = try {
            client.post("http://127.0.0.1:8080/login/phone") {
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
            client.post("http://127.0.0.1:8080/login/email") {
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