package onlytrade.app.viewmodel.login.repository

import onlytrade.app.viewmodel.login.repository.data.remote.LoginApiClient

class LoginRepository(private val loginApiClient: LoginApiClient) {

    suspend fun loginWithPhone(mobileNo: String, pwd: String) = loginApiClient.loginByPhone(mobileNo, pwd)


    suspend fun loginWithEmail(email: String, pwd: String) = loginApiClient.loginByEmail(email, pwd)
}