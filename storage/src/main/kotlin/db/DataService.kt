package info.idoubtthat.db

import info.idoubtthat.models.CitationDAO
import info.idoubtthat.models.UserDAO

interface DataService {
    fun getUser(dao: UserDAO): UserDAO?
    fun createUser(dao: UserDAO)
    fun updateUser(dao: UserDAO)
    fun getCitation(dao: CitationDAO): CitationDAO?
    fun createCitation(dao: CitationDAO)
    fun updateCitation(dao: CitationDAO)
}