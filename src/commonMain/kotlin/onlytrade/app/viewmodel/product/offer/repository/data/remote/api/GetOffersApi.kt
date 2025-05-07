package onlytrade.app.viewmodel.product.offer.repository.data.remote.api

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import onlytrade.app.AppConfig
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.GetOffersResponse

/**
 * Client to the GetOffers web service.
 */
class GetOffersApi(private val appConfig: AppConfig, private val client: HttpClient) {
    suspend fun getOffers(jwtToken: String) = try {
        client.get("${appConfig.baseUrl}/offers") {
            header(
                HttpHeaders.Authorization,
                "Bearer $jwtToken"
            )

        }.body<GetOffersResponse>()
    } catch (e: Exception) {
        Napier.e {
            e.stackTraceToString()
        }
        GetOffersResponse(error = e.message).run {
            if (e is ResponseException) try {
                e.response.body<GetOffersResponse>()
            } catch (e: Exception) {
                copy(error = e.message)
            }
            else this
        }
    }
}