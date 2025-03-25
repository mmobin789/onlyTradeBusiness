package onlytrade.app.viewmodel.login.repository.data.remote

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import onlytrade.app.viewmodel.login.repository.data.remote.model.request.EmailLoginRequest
import onlytrade.app.viewmodel.login.repository.data.remote.model.request.PhoneLoginRequest
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.LoginResponse

class LoginApi(private val client: HttpClient) {

    suspend fun loginByPhone(phone: String, pwd: String) = try {
        val httpResponse = client.post("https://onlytrade.co/login/phone") {
            contentType(ContentType.Application.Json)
            setBody(PhoneLoginRequest(phone, pwd))
        }
        httpResponse.body<LoginResponse>()
    } catch (e: Exception) {
        Napier.e {
            e.stackTraceToString()
        }
        null
    }

    suspend fun loginByEmail(email: String, pwd: String) = try {
        val httpResponse = client.post("https://onlytrade.co/login/email") {
            contentType(ContentType.Application.Json)
            setBody(EmailLoginRequest(email, pwd))
        }
        httpResponse.body<LoginResponse>()
    } catch (e: Exception) {
        Napier.e {
            e.stackTraceToString()
        }
        null

    }
}