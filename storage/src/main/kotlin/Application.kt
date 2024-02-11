package info.idoubtthat


import com.sksamuel.hoplite.ConfigLoader
import info.idoubtthat.db.DataService
import info.idoubtthat.db.DataServiceMysqlImpl
import info.idoubtthat.db.DatabaseManager
import info.idoubtthat.server.Environment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.time.measureTime

val logger: Logger = LoggerFactory.getLogger("Application")

val environment: Environment = ConfigLoader().loadConfigOrThrow("/application.conf")

fun main() {
    val startup = measureTime {
        logger.info("Environment {}", environment)
        val databaseManager = DatabaseManager(environment)
        databaseManager.migrate()
        val dataService: DataService = DataServiceMysqlImpl(databaseManager)
    }
    logger.info("Startup took {}", startup)
}
