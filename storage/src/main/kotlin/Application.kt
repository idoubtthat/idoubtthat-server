package info.idoubtthat


import com.sksamuel.hoplite.ConfigLoader
import info.idoubtthat.db.DataService
import info.idoubtthat.db.DataServiceMysqlImpl
import info.idoubtthat.db.DatabaseManager
import info.idoubtthat.server.API
import info.idoubtthat.server.Environment
import org.glassfish.jersey.jetty.JettyHttpContainerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.ws.rs.core.UriBuilder
import kotlin.time.measureTime

val logger: Logger = LoggerFactory.getLogger("Application")

val environment: Environment = ConfigLoader().loadConfigOrThrow("/application.conf")

fun main() {
    val startup = measureTime {
        logger.info("Environment {}", environment)
        val databaseManager = DatabaseManager(environment)
        databaseManager.migrate()
        val dataService: DataService = DataServiceMysqlImpl(databaseManager)
        val api = API(dataService)
        val url = UriBuilder.fromPath("api")
            .scheme(environment.server.scheme)
            .host(environment.server.host)
            .port(environment.server.port)
            .build()
        val resourceConfig = ResourceConfig().register(api)
        val httpServer = JettyHttpContainerFactory.createServer(
            url,
            resourceConfig,
        )
        httpServer.start()
        logger.info("Server running {}", url)
    }
    logger.info("Startup took {}", startup)
}

