package onlytrade.app.viewmodel.login.repository

import com.russhwolf.settings.Settings
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import onlytrade.app.viewmodel.login.repository.data.LoginConst.JWT_TOKEN
import onlytrade.app.viewmodel.login.repository.data.LoginConst.JWT_USER
import onlytrade.app.viewmodel.login.repository.data.db.User
import onlytrade.app.viewmodel.login.repository.data.remote.api.KycApi
import onlytrade.app.viewmodel.login.repository.data.remote.api.LoginApi
import onlytrade.app.viewmodel.login.repository.data.remote.model.request.KycRequest
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.KycResponse
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.LoginResponse
import onlytrade.db.OnlyTradeDB

class LoginRepository(
    private val loginApi: LoginApi,
    private val kycApi: KycApi,
    private val localPrefs: Settings,
    private val onlyTradeDB: OnlyTradeDB
) {

    companion object {
        private var user: User? = null
        private var jwtToken: String? = null
    }

    /**
     * Returns the authenticated user's JWT if logged in.
     */
    fun jwtToken() = jwtToken ?: localPrefs.getStringOrNull(JWT_TOKEN).also { jwtToken = it }

    /**
     * Returns the authenticated user if logged in.
     */
    fun user() = user ?: localPrefs.getStringOrNull(JWT_USER)?.run {
        Json.decodeFromString<User>(this).also { user = it }

    }

    suspend fun loginWithPhone(mobileNo: String, pwd: String) =
        loginApi.loginByPhone(mobileNo, pwd).also { it.saveLoginInfo() }


    suspend fun loginWithEmail(email: String, pwd: String) =
        loginApi.loginByEmail(email, pwd).also { it.saveLoginInfo() }

    suspend fun uploadDocs(docs: List<ByteArray>) =
        jwtToken()?.let { jwtToken -> kycApi.uploadDocs(jwtToken, KycRequest(docs)) }
            ?: KycResponse(
                statusCode = HttpStatusCode.Unauthorized.value,
                error = HttpStatusCode.Unauthorized.description
            )

    fun isUserLoggedIn() = jwtToken().isNullOrBlank().not()

    fun logOut() {
        localPrefs.clear()
        user = null
        jwtToken = null
        onlyTradeDB.run {
            transaction {
                onlyTradeDB.productQueries.deleteAll()
                onlyTradeDB.offerQueries.deleteAll()
            }
        }
    }


    private fun LoginResponse.saveLoginInfo() {
        jwtToken?.run {
            localPrefs.putString(JWT_TOKEN, this)
        }
        user?.run {
            Json.encodeToString(this).also { user ->
                localPrefs.putString(JWT_USER, user)
            }
        }
    }
}