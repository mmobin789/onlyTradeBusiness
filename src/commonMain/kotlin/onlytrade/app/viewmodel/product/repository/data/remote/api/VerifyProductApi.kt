package onlytrade.app.viewmodel.product.repository.data.remote.api

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
import onlytrade.app.viewmodel.product.repository.data.remote.request.VerifyProductRequest
import onlytrade.app.viewmodel.product.repository.data.remote.response.VerifyProductResponse

/**
 * Client to the verify product web service.
 */
class VerifyProductApi(private val appConfig: AppConfig, private val client: HttpClient) {
    suspend fun verifyProduct(jwtToken: String, productId: Long) = try {
        client.post("${appConfig.baseUrl}/verify/product") {
            header(
                HttpHeaders.Authorization,
                "Bearer $jwtToken"
            )
            contentType(ContentType.Application.Json)
            setBody(VerifyProductRequest(productId))
        }.body<VerifyProductResponse>()
    } catch (e: Exception) {
        Napier.e {
            e.stackTraceToString()
        }
        VerifyProductResponse(error = e.message).run {
            if (e is ResponseException) try {
                e.response.body<VerifyProductResponse>()
            } catch (e: Exception) {
                copy(error = e.message)
            }
            else this
        }
    }
}