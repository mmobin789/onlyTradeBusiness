package onlytrade.app.viewmodel.login.repository.data.remote.model.response

import kotlinx.serialization.Serializable
import onlytrade.app.viewmodel.login.repository.data.db.User

@Serializable
data class LoginResponse(
    val statusCode: Int? = null,
    val jwtToken: String? = null,
    val user: User? = null,
    val error: String? = null
)