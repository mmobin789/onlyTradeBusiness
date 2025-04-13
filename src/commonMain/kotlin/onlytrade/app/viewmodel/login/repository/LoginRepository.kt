package onlytrade.app.viewmodel.login.repository

import com.russhwolf.settings.Settings
import kotlinx.serialization.json.Json
import onlytrade.app.viewmodel.login.repository.data.LoginConst.JWT_TOKEN
import onlytrade.app.viewmodel.login.repository.data.LoginConst.JWT_USER
import onlytrade.app.viewmodel.login.repository.data.remote.LoginApi
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.LoginResponse

class LoginRepository(private val loginApi: LoginApi, private val localPrefs: Settings) {

    suspend fun loginWithPhone(mobileNo: String, pwd: String) =
        loginApi.loginByPhone(mobileNo, pwd)?.also { it.saveLoginInfo() }


    suspend fun loginWithEmail(email: String, pwd: String) =
        loginApi.loginByEmail(email, pwd)?.also { it.saveLoginInfo() }


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