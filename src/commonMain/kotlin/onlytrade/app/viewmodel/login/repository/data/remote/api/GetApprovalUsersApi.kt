package onlytrade.app.viewmodel.login.repository.data.remote.api

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import onlytrade.app.AppConfig
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.GetApprovalUsersResponse

/**
 * Client to the get approval users web service.
 */
class GetApprovalUsersApi(private val appConfig: AppConfig, private val client: HttpClient) {
    suspend fun getApprovalUsers(jwtToken: String) = try {
        client.get("${appConfig.baseUrl}/approval/users") {
            header(
                HttpHeaders.Authorization,
                "Bearer $jwtToken"
            )
        }.body<GetApprovalUsersResponse>()
    } catch (e: Exception) {
        Napier.e {
            e.stackTraceToString()
        }
        GetApprovalUsersResponse(error = e.message).run {
            if (e is ResponseException) try {
                e.response.body<GetApprovalUsersResponse>()
            } catch (e: Exception) {
                copy(error = e.message)
            }
            else this
        }
    }
}