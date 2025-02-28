package onlytrade.app.viewmodel.login.repository.data.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class EmailLoginRequest(val email: String, val password: String)
