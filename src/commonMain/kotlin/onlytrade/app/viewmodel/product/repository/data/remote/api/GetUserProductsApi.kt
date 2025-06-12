package onlytrade.app.viewmodel.product.repository.data.remote.api

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import onlytrade.app.AppConfig
import onlytrade.app.viewmodel.product.repository.data.remote.response.GetProductsResponse

/**
 * Client to the GetProducts web service.
 */
class GetUserProductsApi(private val appConfig: AppConfig, private val client: HttpClient) {

    suspend fun getUserProducts(
        jwtToken: String,
        pageNo: Int,
        pageSize: Int
    ) =
        try {
            val url = URLBuilder("${appConfig.baseUrl}/user/products").apply {
                parameters.append("size", pageSize.toString())
                parameters.append("page", pageNo.toString())
            }.toString()

            client.get(url) {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $jwtToken"
                )
            }.body<GetProductsResponse>()
        } catch (e: Exception) {
            Napier.e {
                e.stackTraceToString()
            }
            GetProductsResponse(error = e.message).run {
                if (e is ResponseException) try {
                    e.response.body<GetProductsResponse>()
                } catch (e: Exception) {
                    copy(error = e.message)
                }
                else this
            }
        }
}