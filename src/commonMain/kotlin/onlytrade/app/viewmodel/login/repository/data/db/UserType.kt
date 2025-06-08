package onlytrade.app.viewmodel.login.repository.data.db

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Enums correspond to ordinal values.
 */
@Serializable
enum class UserType {
    @SerialName("0")
    ADMIN,

    @SerialName("1")
    CUSTOMER
}