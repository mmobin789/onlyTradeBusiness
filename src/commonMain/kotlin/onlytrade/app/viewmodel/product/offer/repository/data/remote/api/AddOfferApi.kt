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
import onlytrade.app.viewmodel.product.offer.repository.data.remote.request.AddOfferRequest
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.AddOfferResponse

/**
 * client to the AddOffer web service.
 */
class AddOfferApi(private val client: HttpClient) {
    suspend fun addOffer(addOfferRequest: AddOfferRequest, jwtToken: String) = try {
        val httpResponse = client.post("https://onlytrade.co/offer/add") {
            header(
                HttpHeaders.Authorization,
                "Bearer $jwtToken"
            )
            contentType(ContentType.Application.Json)
            setBody(addOfferRequest)
        }
        httpResponse.body<AddOfferResponse>()
    } catch (e: Exception) {
        Napier.e {
            e.stackTraceToString()
        }
        AddOfferResponse(error = e.message).run {
            if (e is ResponseException)
                try {
                    e.response.body<AddOfferResponse>()
                } catch (e: Exception) {
                    copy(error = e.message)
                }
            else this
        }
    }
}