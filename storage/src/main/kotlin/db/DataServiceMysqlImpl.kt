package info.idoubtthat.db

import info.idoubtthat.db.op.citation.CreateCitationOp
import info.idoubtthat.db.op.citation.GetCitationOp
import info.idoubtthat.db.op.citation.UpdateCitationOp
import info.idoubtthat.db.op.user.CreateUserOp
import info.idoubtthat.db.op.user.GetUserOp
import info.idoubtthat.db.op.user.UpdateUserOp
import info.idoubtthat.models.CitationDAO
import info.idoubtthat.models.UserDAO

class DataServiceMysqlImpl(private val databaseManager: DatabaseManager): DataService {
    override fun getUser(dao: UserDAO): UserDAO? {
        return databaseManager.read(GetUserOp(dao))
    }

    override fun createUser(dao: UserDAO) {
        databaseManager.write(CreateUserOp(dao))
    }

    override fun updateUser(dao: UserDAO) {
        databaseManager.write(UpdateUserOp(dao))
    }

    override fun getCitation(dao: CitationDAO): CitationDAO? {
        return databaseManager.read(GetCitationOp(dao))
    }

    override fun createCitation(dao: CitationDAO) {
        databaseManager.write(CreateCitationOp(dao))
    }

    override fun updateCitation(dao: CitationDAO) {
        databaseManager.write(UpdateCitationOp(dao))
    }
}