package info.idoubtthat.server

data class Environment(val server: ServerConfig = ServerConfig(), val db: DatabaseConfig = DatabaseConfig())
data class ServerConfig(val scheme: String = "http", val host: String = "0.0.0.0", val port: Int = 8080)
data class DatabaseConfig(val host: String = "localhost", val port: Int = 3306)
