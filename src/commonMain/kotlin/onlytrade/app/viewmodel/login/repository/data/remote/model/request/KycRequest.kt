package onlytrade.app.viewmodel.login.repository.data.remote.model.request

data class KycRequest(val name: String, val photoId: ByteArray, val photo: ByteArray) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as KycRequest

        if (!photoId.contentEquals(other.photoId)) return false
        if (!photo.contentEquals(other.photo)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = photoId.contentHashCode()
        result = 31 * result + photo.contentHashCode()
        return result
    }
}