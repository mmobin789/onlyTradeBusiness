package onlytrade.app.viewmodel.product.offer.repository.data.remote.api

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
import onlytrade.app.viewmodel.product.offer.repository.data.remote.request.GetOfferReceivedRequest
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.GetOfferResponse

/**
 * client to the check offer received web service.
 */
class GetOfferReceivedApi(private val appConfig: AppConfig, private val client: HttpClient) {
    suspend fun getOfferReceived(
        getOfferReceivedRequest: GetOfferReceivedRequest,
        jwtToken: String
    ) = try {
        val httpResponse = client.post("${appConfig.baseUrl}/offer/received") {
            header(
                HttpHeaders.Authorization,
                "Bearer $jwtToken"
            )
            contentType(ContentType.Application.Json)
            setBody(getOfferReceivedRequest)
        }
        httpResponse.body<GetOfferResponse>()
    } catch (e: Exception) {
        Napier.e {
            e.stackTraceToString()
        }
        GetOfferResponse(error = e.message).run {
            if (e is ResponseException)
                try {
                    e.response.body<GetOfferResponse>()
                } catch (e: Exception) {
                    copy(error = e.message)
                }
            else this
        }
    }
}