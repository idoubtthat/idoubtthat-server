package idoubtthat.values

import java.time.Instant


data class Resource(
    val assertion: String? = null,
    val url: String? = null,
    val date: Instant? = null,
    val type: Int? = null
)

data class ValidityClaims(
    val isTrue: Boolean? = null,
    val isSupported: Boolean? = null,
    val isGood: Boolean? = null
)
data class Signable(
    val payload: Resource,
    val signedAt: Instant,
    val signedBy: String,
    val claims: ValidityClaims
)

data class SignedReource(
    val content: Signable,
    val signature: String
)