package onlytrade.app.viewmodel.login.repository

import com.russhwolf.settings.Settings
import kotlinx.serialization.json.Json
import onlytrade.app.viewmodel.login.repository.data.LoginConst.JWT_TOKEN
import onlytrade.app.viewmodel.login.repository.data.LoginConst.JWT_USER
import onlytrade.app.viewmodel.login.repository.data.remote.LoginApi

class LoginRepository(private val loginApi: LoginApi, private val settings: Settings) {

    suspend fun loginWithPhone(mobileNo: String, pwd: String) =
        loginApi.loginByPhone(mobileNo, pwd)?.also {
            it.jwtToken?.run {
                settings.putString(JWT_TOKEN, this)
            }
            it.user?.run {
                val user = Json.encodeToString(this)
                settings.putString(JWT_USER, user)
            }
        }


    suspend fun loginWithEmail(email: String, pwd: String) = loginApi.loginByEmail(email, pwd)
}