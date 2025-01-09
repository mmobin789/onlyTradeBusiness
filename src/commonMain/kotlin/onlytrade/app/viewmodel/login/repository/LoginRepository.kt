package onlytrade.app.viewmodel.login.repository

import onlytrade.app.viewmodel.login.repository.data.remote.LoginWebApi

class LoginRepository(private val loginWebApi: LoginWebApi) {

    suspend fun loginWithPhone(mobileNo: String, pwd: String) = loginWebApi.greeting()


    suspend fun loginWithEmail(email: String, pwd: String) = loginWebApi.greeting()
}