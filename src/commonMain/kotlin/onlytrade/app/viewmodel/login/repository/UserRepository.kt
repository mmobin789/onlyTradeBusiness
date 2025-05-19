package onlytrade.app.viewmodel.login.repository

import com.russhwolf.settings.Settings
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import onlytrade.app.viewmodel.login.repository.data.LoginConst.JWT_USER
import onlytrade.app.viewmodel.login.repository.data.db.User
import onlytrade.app.viewmodel.login.repository.data.remote.api.GetUserDetailApi
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.GetUserDetailResponse
import onlytrade.db.OnlyTradeDB

class UserRepository(
    private val loginRepository: LoginRepository,
    private val localPrefs: Settings,
    private val getUserDetailApi: GetUserDetailApi,
    onlyTradeDB: OnlyTradeDB
) {
    private val dao = onlyTradeDB.userQueries

    suspend fun getUserDetail(userId: Long) = loginRepository.jwtToken()?.let {
        getUserById(userId)?.let { localUser ->
            GetUserDetailResponse(
                user = localUser.let(::toUser),
                statusCode = HttpStatusCode.OK.value
            )
        } ?: getUserDetailApi.getUserDetail(it, userId).apply {
            saveLoggedInUserInfo()
            user?.also(::addUser)
        }
    } ?: GetUserDetailResponse(statusCode = HttpStatusCode.Unauthorized.value)

    private fun GetUserDetailResponse.saveLoggedInUserInfo() =
        user?.run {
            if (appUser(id)) Json.encodeToString(this).also { user ->
                localPrefs.putString(JWT_USER, user)
            }
        }

    private fun appUser(id: Long) = loginRepository.user()?.id == id

    private fun getUserById(id: Long) =
        dao.transactionWithResult { dao.getById(id).executeAsOneOrNull() }

    private fun addUser(user: User) = user.run {
        dao.transaction {
            dao.insert(id, phone, email, name, verified, docs, createdAt, updatedAt)
        }
    }

    private fun toUser(user: onlytrade.db.User) = user.run {
        User(
            id = id,
            phone = phone,
            email = email,
            password = null,
            name = name,
            verified = verified,
            docs = docs,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}