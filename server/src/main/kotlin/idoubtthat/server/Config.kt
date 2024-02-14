package idoubtthat.server

import db.DatabaseConfig
import io.ktor.server.config.*
import io.ktor.server.config.ConfigLoader.Companion.load
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class ServerConfig(val host: String ="0.0.0.0", val port: Int = 8080)

data class Config(
    val serverConfig: ServerConfig = ServerConfig(),
    val databaseConfig: DatabaseConfig = DatabaseConfig()
)


private val logger: Logger = LoggerFactory.getLogger(Config::class.java)

fun load(path: String = "application.conf"): Config {
    val applicationConfig: ApplicationConfig = ConfigLoader.load("application.conf")
    val config = Config(
        ServerConfig(
            applicationConfig.property("server.host").getString(),
            applicationConfig.property("server.port").getString().toInt()
        ),
        DatabaseConfig(
            applicationConfig.property("db.host").getString(),
            applicationConfig.property("db.port").getString().toInt()

        )
    )

    logger.info("Config {}", config)
    return config
}

