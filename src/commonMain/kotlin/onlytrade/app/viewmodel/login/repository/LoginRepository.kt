package onlytrade.app.viewmodel.login.repository

import onlytrade.app.viewmodel.login.repository.data.remote.LoginApi

class LoginRepository(private val loginApi: LoginApi) {

    suspend fun loginWithPhone(mobileNo: String, pwd: String) = loginApi.loginByPhone(mobileNo, pwd)

    suspend fun loginWithEmail(email: String, pwd: String) = loginApi.loginByEmail(email, pwd)
}