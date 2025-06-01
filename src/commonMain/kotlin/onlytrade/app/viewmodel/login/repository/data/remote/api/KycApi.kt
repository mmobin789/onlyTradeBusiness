package onlytrade.app.viewmodel.login.repository.data.remote.api

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import onlytrade.app.AppConfig
import onlytrade.app.viewmodel.login.repository.data.remote.model.request.KycRequest
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.KycResponse

/**
 * client to the user KYC web service.
 */
class KycApi(private val appConfig: AppConfig, private val client: HttpClient) {

    suspend fun uploadDocs(jwtToken: String, kycRequest: KycRequest) =
        try {
            val httpResponse = client.post("${appConfig.baseUrl}/kyc") {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $jwtToken"
                )
                setBody(MultiPartFormDataContent(formData {
                    kycRequest.docs.forEachIndexed { index, byteArray ->
                        //product Images are guaranteed to be non null here.
                        val key = "userDoc${index + 1}"
                        append(key, byteArray, Headers.build {
                            append(
                                HttpHeaders.ContentType,
                                "image/jpeg"
                            ) // ✅ Correct Content-Type
                            append(
                                HttpHeaders.ContentDisposition,
                                "form-data; name=\"userDoc\"; filename=\"$key.jpg\""
                            ) // ✅ Ensures it's treated as a file
                        })
                    }
                }))
            }
            httpResponse.body<KycResponse>()
        } catch (e: Exception) {
            Napier.e {
                e.stackTraceToString()
            }
            KycResponse(error = e.message).run {
                if (e is ResponseException) try {
                    e.response.body<KycResponse>()
                } catch (e: Exception) {
                    copy(error = e.message)
                }
                else this
            }
        }
}