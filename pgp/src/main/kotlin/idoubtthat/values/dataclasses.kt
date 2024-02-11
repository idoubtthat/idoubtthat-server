package idoubtthat.values

import java.time.Instant


enum class ResourceType {
    TEXT,
    GENERIC_URL,
    GITHUB,
    YOUTUBE,
    TWITTER
}

data class Resource(
    val assertion: String? = null,
    val url: String? = null,
    val date: Instant? = null,
    val type: ResourceType? = null
)

data class ValidityClaims(
    val isTrue: Boolean? = null,
    val isSupported: Boolean? = null,
    val isGood: Boolean? = null
)

data class User(
    val userId: String,
    val publicKeyIds: Set<Long>,
    val onlineSecretKey: Long?
)

data class Signable(
    val payload: Resource,
    val signedAt: Instant,
    val signedBy: String,
    val claims: ValidityClaims
) {
    fun write(): String = Serializer.write(this)
}

data class SignedReource(
    val content: Signable,
    val signature: String
)