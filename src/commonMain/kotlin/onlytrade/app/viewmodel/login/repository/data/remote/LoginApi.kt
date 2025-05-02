package onlytrade.app.viewmodel.login.repository.data.remote

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import onlytrade.app.AppConfig
import onlytrade.app.viewmodel.login.repository.data.remote.model.request.EmailLoginRequest
import onlytrade.app.viewmodel.login.repository.data.remote.model.request.PhoneLoginRequest
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.LoginResponse

/**
 * client to the login web services.
 */
class LoginApi(appConfig: AppConfig, private val client: HttpClient) {

    private val baseUrl = appConfig.baseUrl

    suspend fun loginByPhone(phone: String, pwd: String) = try {
        val httpResponse = client.post("$baseUrl/login/phone") {
            contentType(ContentType.Application.Json)
            setBody(PhoneLoginRequest(phone, pwd))
        }
        httpResponse.body<LoginResponse>()
    } catch (e: Exception) {
        Napier.e {
            e.stackTraceToString()
        }
        getLoginError(e)
    }

    suspend fun loginByEmail(email: String, pwd: String) = try {
        val httpResponse = client.post("$baseUrl/login/email") {
            contentType(ContentType.Application.Json)
            setBody(EmailLoginRequest(email, pwd))
        }
        httpResponse.body<LoginResponse>()
    } catch (e: Exception) {
        Napier.e {
            e.stackTraceToString()
        }
        getLoginError(e)
    }

    private suspend fun getLoginError(e: Exception) =
        LoginResponse(error = e.message).run {
            if (e is ResponseException)
                try {
                    e.response.body<LoginResponse>()
                } catch (e: Exception) {
                    copy(error = e.message)
                }
            else this
        }
}