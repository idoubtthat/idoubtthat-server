package db

import db.op.citation.CreateCitationOp
import db.op.citation.GetCitationOp
import db.op.citation.UpdateCitationOp
import db.op.user.UpdateUserOp
import db.models.CitationDAO
import db.models.UserDAO
import db.op.user.CreateUserOp
import db.op.user.GetUserOp
import info.idoubtthat.db.DataService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.time.measureTime

class DataServiceMysqlImpl(databaseConfig: DatabaseConfig): DataService {
    private val databaseManager: DatabaseManager = DatabaseManager(databaseConfig)
    private val logger: Logger = LoggerFactory.getLogger(DataServiceMysqlImpl::class.java)

    init {
        val migration = measureTime {
            databaseManager.migrate()
        }
        logger.info("Migration completed in {}", migration)
    }

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