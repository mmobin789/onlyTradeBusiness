package onlytrade.app.viewmodel.login.repository.data.db

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val phone: String?,
    val email: String?,
    val password: String?,
    val name: String?,
    val verified: Boolean,
    val docs: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val userType: UserType
)
