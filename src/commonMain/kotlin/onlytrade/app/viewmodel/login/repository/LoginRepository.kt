package onlytrade.app.viewmodel.login.repository

import onlytrade.app.viewmodel.login.repository.data.remote.LoginWebApi

class LoginRepository(private val loginWebApi: LoginWebApi) {

    suspend fun loginWithPhone(phoneNumber: String) {
        loginWebApi.greeting()
    }

    fun loginWithEmail(email: String) {

    }
}