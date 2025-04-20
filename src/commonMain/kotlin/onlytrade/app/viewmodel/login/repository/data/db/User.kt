package onlytrade.app.viewmodel.login.repository.data.db

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val phone: String?,
    val email: String?,
    val password: String,
    val name: String?,
    val verified: Boolean,
    val loggedIn: Boolean
)
