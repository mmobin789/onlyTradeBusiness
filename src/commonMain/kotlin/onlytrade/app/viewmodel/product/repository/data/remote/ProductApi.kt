package onlytrade.app.viewmodel.product.repository.data.remote

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.basicAuth
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import onlytrade.app.viewmodel.product.add.repository.data.remote.request.AddProductRequest
import onlytrade.app.viewmodel.product.add.repository.data.remote.response.AddProductResponse

class ProductApi(private val client: HttpClient) { //todo

    suspend fun addProduct(addProductRequest: AddProductRequest) = try {
        val httpResponse = client.post("https://onlytrade.co/product/add") {
            basicAuth("03217004104", "0000000")
            setBody(MultiPartFormDataContent(formData {
                append(
                    "addProductRequest",
                    Json.encodeToString(addProductRequest),
                    Headers.build {
                        append(HttpHeaders.ContentType, "application/json")
                    })
                addProductRequest.productImages!!.forEach { //product Images are guaranteed to be non null here.
                    append("productImages", it)
                }
            }))
        }
        httpResponse.body<AddProductResponse>()
    } catch (e: Exception) {
        Napier.e {
            e.stackTraceToString()
        }
        null
    }
}