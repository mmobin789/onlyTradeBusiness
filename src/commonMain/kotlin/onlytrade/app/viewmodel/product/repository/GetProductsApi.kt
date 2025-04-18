package onlytrade.app.viewmodel.product.repository

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.http.parameters
import onlytrade.app.viewmodel.product.repository.data.remote.response.GetProductsResponse

/**
 * Client to the GetProducts web service.
 */
class GetProductsApi(private val client: HttpClient) {

    suspend fun getProducts(pageNo: Int, pageSize: Int, userId: Int? = null) = try {
        client.get("https://onlytrade.co/products") {
            parameters {
                append("size", pageSize.toString())
                append("page", pageNo.toString())
                userId?.let { userId ->
                    append("uid", userId.toString())
                }
            }

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