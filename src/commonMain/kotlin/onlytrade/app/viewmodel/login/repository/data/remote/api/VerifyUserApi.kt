package onlytrade.app.viewmodel.login.repository.data.remote.api

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import onlytrade.app.AppConfig
import onlytrade.app.viewmodel.login.repository.data.remote.model.request.VerifyUserRequest
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.VerifyUserResponse

/**
 * Client to the verify user web service.
 */
class VerifyUserApi(private val appConfig: AppConfig, private val client: HttpClient) {
    suspend fun verifyUser(jwtToken: String, userId: Long) = try {
        client.post("${appConfig.baseUrl}/verify/user") {
            header(
                HttpHeaders.Authorization,
                "Bearer $jwtToken"
            )
            contentType(ContentType.Application.Json)
            setBody(VerifyUserRequest(userId))
        }.body<VerifyUserResponse>()
    } catch (e: Exception) {
        Napier.e {
            e.stackTraceToString()
        }
        VerifyUserResponse(error = e.message).run {
            if (e is ResponseException) try {
                e.response.body<VerifyUserResponse>()
            } catch (e: Exception) {
                copy(error = e.message)
            }
            else this
        }
    }
}