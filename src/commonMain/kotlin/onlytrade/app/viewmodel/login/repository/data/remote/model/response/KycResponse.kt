package onlytrade.app.viewmodel.login.repository.data.remote.model.response

import kotlinx.serialization.Serializable

@Serializable
data class KycResponse(val statusCode: Int? = null, val error: String? = null)