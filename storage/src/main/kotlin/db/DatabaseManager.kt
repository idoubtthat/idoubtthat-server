package db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import db.op.ReadOp
import org.flywaydb.core.Flyway
import org.jooq.TransactionalRunnable
import org.jooq.impl.DSL

class DatabaseManager(val databaseConfig: DatabaseConfig) {
    private val config = HikariConfig().apply {
        jdbcUrl = "jdbc:mysql://${databaseConfig.host}:${databaseConfig.port}/citation"
        username = "root"
        password = "secret"
        maxLifetime = 1_200_000
        initializationFailTimeout = 1000 * 60 * 3
        maximumPoolSize = 4
        isAutoCommit = false
        addDataSourceProperty("rewriteBatchedStatements", "true")
        addDataSourceProperty("useSSL", "true")
        addDataSourceProperty("requireSSL", "true")
        addDataSourceProperty("enabledTLSProtocols", "TLSv1.2")
        addDataSourceProperty("useServerPrepStmts","false")
    }

    private val datasource = HikariDataSource(config)

    fun migrate() {
        val flyway: Flyway = Flyway.configure()
            .dataSource(datasource)
            .locations("classpath:db/migration")
            .loggers("slf4j")
            .load()
        // Start the migration
        flyway.migrate()
    }

    fun <T: TransactionalRunnable> write(op: T) {
        val connection = datasource.connection
        val dsl =  DSL.using(connection)
        dsl.transaction(op)
    }


    fun <T> read(op: ReadOp<T>): T {
        val connection = datasource.connection
        val dsl = DSL.using(connection)
        return op.run(dsl)
    }
}