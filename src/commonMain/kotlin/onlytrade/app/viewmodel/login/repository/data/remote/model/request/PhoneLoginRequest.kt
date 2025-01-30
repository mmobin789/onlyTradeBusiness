package onlytrade.app.viewmodel.login.repository.data.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class PhoneLoginRequest(val phone: String, val password: String) //todo
