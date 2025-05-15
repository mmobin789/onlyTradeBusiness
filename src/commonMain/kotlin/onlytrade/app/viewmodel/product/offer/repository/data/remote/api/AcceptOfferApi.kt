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
import onlytrade.app.viewmodel.product.offer.repository.data.remote.request.AcceptOfferRequest
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.AcceptOfferResponse

/**
 * Client to the AcceptOffer web service.
 */
class AcceptOfferApi(private val appConfig: AppConfig, private val client: HttpClient) {
    suspend fun acceptOffer(jwtToken: String, offerId: Long) = try {
        client.post("${appConfig.baseUrl}/offer/accept") {
            header(
                HttpHeaders.Authorization,
                "Bearer $jwtToken"
            )
            contentType(ContentType.Application.Json)
            setBody(AcceptOfferRequest(offerId))
        }.body<AcceptOfferResponse>()
    } catch (e: Exception) {
        Napier.e {
            e.stackTraceToString()
        }
        AcceptOfferResponse(error = e.message).run {
            if (e is ResponseException) try {
                e.response.body<AcceptOfferResponse>()
            } catch (e: Exception) {
                copy(error = e.message)
            }
            else this
        }
    }
}