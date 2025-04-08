package onlytrade.app.viewmodel.login.repository.data.remote.model.response

import kotlinx.serialization.Serializable
import onlytrade.app.viewmodel.login.repository.data.db.User

@Serializable
data class LoginResponse(val status: Int, val jwtToken: String?, val user: User?)