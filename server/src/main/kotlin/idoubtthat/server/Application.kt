package idoubtthat.server
import db.DataServiceMysqlImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.time.measureTime



fun main() {
    val config = load()
    val dataservice = DataServiceMysqlImpl(config.databaseConfig)
    val restService = RestService(config.serverConfig, dataservice)
}
