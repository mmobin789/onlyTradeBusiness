package onlytrade.app.viewmodel.product.add.repository.data.remote.model.response

import kotlinx.serialization.Serializable

@Serializable
data class AddProductResponse(val msg: String, val status: String)