package onlytrade.app.viewmodel.product.offer.repository.data.remote.api

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import onlytrade.app.AppConfig
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.DeleteOfferResponse

/**
 * Client to the DeleteOffer web service.
 */
class DeleteOfferApi(private val appConfig: AppConfig, private val client: HttpClient) {
    suspend fun deleteOffer(jwtToken: String, offerId: Long) = try {
        client.delete("${appConfig.baseUrl}/offer/$offerId") {
            header(
                HttpHeaders.Authorization,
                "Bearer $jwtToken"
            )
        }.body<DeleteOfferResponse>()
    } catch (e: Exception) {
        Napier.e {
            e.stackTraceToString()
        }
        DeleteOfferResponse(error = e.message).run {
            if (e is ResponseException) try {
                e.response.body<DeleteOfferResponse>()
            } catch (e: Exception) {
                copy(error = e.message)
            }
            else this
        }
    }
}