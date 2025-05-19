package onlytrade.app.viewmodel.login.repository.data.remote.api

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import onlytrade.app.AppConfig
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.GetUserDetailResponse

/**
 * Client to the user detail web service.
 */
class GetUserDetailApi(private val appConfig: AppConfig, private val client: HttpClient) {
    suspend fun getUserDetail(jwtToken: String, userId: Long) = try {
        client.get("${appConfig.baseUrl}/user/$userId") {
            header(
                HttpHeaders.Authorization,
                "Bearer $jwtToken"
            )
        }.body<GetUserDetailResponse>()
    } catch (e: Exception) {
        Napier.e {
            e.stackTraceToString()
        }
        GetUserDetailResponse(error = e.message).run {
            if (e is ResponseException) try {
                e.response.body<GetUserDetailResponse>()
            } catch (e: Exception) {
                copy(error = e.message)
            }
            else this
        }
    }
}