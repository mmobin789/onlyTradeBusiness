package onlytrade.app.viewmodel.login.repository

import com.russhwolf.settings.Settings
import io.ktor.http.HttpStatusCode
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import onlytrade.app.viewmodel.login.repository.data.LoginConst.JWT_USER
import onlytrade.app.viewmodel.login.repository.data.db.User
import onlytrade.app.viewmodel.login.repository.data.db.UserType
import onlytrade.app.viewmodel.login.repository.data.remote.api.GetApprovalUsersApi
import onlytrade.app.viewmodel.login.repository.data.remote.api.GetUserDetailApi
import onlytrade.app.viewmodel.login.repository.data.remote.api.KycApi
import onlytrade.app.viewmodel.login.repository.data.remote.api.VerifyUserApi
import onlytrade.app.viewmodel.login.repository.data.remote.model.request.KycRequest
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.GetApprovalUsersResponse
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.GetUserDetailResponse
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.KycResponse
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.VerifyUserResponse
import onlytrade.db.OnlyTradeDB

class UserRepository(
    private val loginRepository: LoginRepository,
    private val localPrefs: Settings,
    private val kycApi: KycApi,
    private val getUserDetailApi: GetUserDetailApi,
    private val verifyUserApi: VerifyUserApi,
    private val getApprovalUsersApi: GetApprovalUsersApi,
    onlyTradeDB: OnlyTradeDB
) {
    private val dao = onlyTradeDB.userQueries


    suspend fun getApprovalUsers() =
        loginRepository.jwtToken()?.let { jwtToken ->
            getApprovalUsersApi.getApprovalUsers(jwtToken)
        } ?: GetApprovalUsersResponse(statusCode = HttpStatusCode.Unauthorized.value)


    suspend fun verifyUser(userId: Long) =
        loginRepository.jwtToken()?.let { jwtToken ->
            verifyUserApi.verifyUser(jwtToken, userId)
        } ?: VerifyUserResponse(statusCode = HttpStatusCode.Unauthorized.value)


    suspend fun getUserDetail(userId: Long) =
        loginRepository.jwtToken()?.let {
            getUserById(userId)?.let { localUser ->
                val user = localUser.let(::toUser)
                if (user.verified)
                    GetUserDetailResponse(
                        user = user,
                        statusCode = HttpStatusCode.OK.value
                    ) else getUserDetailApi(it, userId)
            } ?: getUserDetailApi(it, userId)
        } ?: GetUserDetailResponse(statusCode = HttpStatusCode.Unauthorized.value)

    private suspend fun getUserDetailApi(jwtToken: String, userId: Long) =
        getUserDetailApi.getUserDetail(jwtToken, userId).apply {
            saveLoggedInUserInfo()
            user?.also(::addUser)
        }

    suspend fun uploadDocs(name: String, photoId: ByteArray, photo: ByteArray) =
        loginRepository.jwtToken()
            ?.let { jwtToken -> kycApi.uploadDocs(jwtToken, KycRequest(name, photoId, photo)) }
            ?: KycResponse(
                statusCode = HttpStatusCode.Unauthorized.value,
                error = HttpStatusCode.Unauthorized.description
            )

    private fun GetUserDetailResponse.saveLoggedInUserInfo() =
        user?.run {
            if (appUser(id)) Json.encodeToString(this).also { user ->
                localPrefs.putString(JWT_USER, user)
            }
        }

    private fun appUser(id: Long) = loginRepository.user()?.id == id

    private fun getUserById(id: Long) =
        dao.transactionWithResult { dao.getById(id).executeAsOneOrNull() }

    private fun addUser(user: User) = dao.transaction {
        user.run {
            dao.insert(
                id,
                phone,
                email,
                name,
                verified,
                true,
                photoId,
                photo,
                createdAt.toString(),
                updatedAt.toString(),
                userType.ordinal.toLong()
            )
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
            photoId = photoId,
            photo = photo,
            createdAt = LocalDateTime.parse(createdAt),
            updatedAt = LocalDateTime.parse(updatedAt),
            userType = if (user.userType == 0L) UserType.ADMIN else UserType.CUSTOMER
        )
    }
}