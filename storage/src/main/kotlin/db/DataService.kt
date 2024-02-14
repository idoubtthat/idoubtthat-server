package info.idoubtthat.db

import db.models.CitationDAO
import db.models.UserDAO

interface DataService {
    fun getUser(dao: UserDAO): UserDAO?
    fun createUser(dao: UserDAO)
    fun updateUser(dao: UserDAO)
    fun getCitation(dao: CitationDAO): CitationDAO?
    fun createCitation(dao: CitationDAO)
    fun updateCitation(dao: CitationDAO)
}