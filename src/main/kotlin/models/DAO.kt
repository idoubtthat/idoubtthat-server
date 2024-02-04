package info.idoubtthat.models

import java.util.*

abstract class DAO(id: UUID?)

data class UserDAO(val id: UUID? = null, val firstName: String? = null, val lastName: String? = null): DAO(id)
data class CitationDAO(val id: UUID? = null, val userId: UUID? = null, val url: String? = null, val commentary: String? = null): DAO(id)
