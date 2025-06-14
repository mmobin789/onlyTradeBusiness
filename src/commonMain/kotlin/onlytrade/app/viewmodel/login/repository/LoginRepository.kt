package onlytrade.app.viewmodel.login.repository

import com.russhwolf.settings.Settings
import onlytrade.app.AppConfig.Json
import onlytrade.app.viewmodel.login.repository.data.LoginConst.JWT_TOKEN
import onlytrade.app.viewmodel.login.repository.data.LoginConst.JWT_USER
import onlytrade.app.viewmodel.login.repository.data.db.User
import onlytrade.app.viewmodel.login.repository.data.remote.api.LoginApi
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.LoginResponse
import onlytrade.db.OnlyTradeDB

class LoginRepository(
    private val loginApi: LoginApi,
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

    fun isUserLoggedIn() = jwtToken().isNullOrBlank().not()

    fun logOut() {
        localPrefs.clear()
        user = null
        jwtToken = null
        onlyTradeDB.run {
            transaction {
                onlyTradeDB.userQueries.deleteAll()
                onlyTradeDB.productQueries.deleteAll()
                onlyTradeDB.offerQueries.deleteAll()
                onlyTradeDB.offerProductQueries.deleteAll()
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