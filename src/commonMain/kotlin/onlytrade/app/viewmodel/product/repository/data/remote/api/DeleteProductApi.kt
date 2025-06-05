package onlytrade.app.viewmodel.product.repository.data.remote.api

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import onlytrade.app.AppConfig
import onlytrade.app.viewmodel.product.repository.data.remote.response.DeleteProductResponse

/**
 * Client to the DeleteProduct web service.
 */
class DeleteProductApi(private val appConfig: AppConfig, private val client: HttpClient) {
    suspend fun deleteProduct(jwtToken: String, productId: Long) = try {
        client.delete("${appConfig.baseUrl}/product/$productId") {
            header(
                HttpHeaders.Authorization,
                "Bearer $jwtToken"
            )
        }.body<DeleteProductResponse>()
    } catch (e: Exception) {
        Napier.e {
            e.stackTraceToString()
        }
        DeleteProductResponse(error = e.message).run {
            if (e is ResponseException) try {
                e.response.body<DeleteProductResponse>()
            } catch (e: Exception) {
                copy(error = e.message)
            }
            else this
        }
    }
}