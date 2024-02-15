package idoubtthat.server

import db.DataServiceMysqlImpl
import db.DatabaseConfig
import info.idoubtthat.db.DataService
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import java.time.Duration
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RestServiceTest {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson()
        }
        install(Logging)
        expectSuccess = true
        defaultRequest {
            url("http://localhost:8080/api/v1/")
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 1000
        }
    }

    @Test
    fun testSwagger() {
        runBlocking {
            val response = client.get("swagger")
            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

    @Test
    fun testOpenapi() {
        runBlocking {
            val response = client.get("openapi")
            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

    @Test
    fun testUserCRUD() {
        runBlocking {
            val user = User(
                UUID.randomUUID(),
                "Sage",
                "Smith"
            )
            val createResponse = client.post("user") {
                contentType(ContentType.Application.Json)
                setBody(user)
            }
            assertEquals(HttpStatusCode.OK, createResponse.status)
            val getResponse = client.get("user/${user.id}")
            assertEquals(HttpStatusCode.OK, getResponse.status)
        }
    }

    @Test
    fun testCitationCRUD() {
        runBlocking {
            val user = User(
                UUID.randomUUID(),
                "Sage",
                "Smith"
            )
            val createResponse = client.post("user") {
                contentType(ContentType.Application.Json)
                setBody(user)
            }
            assertEquals(HttpStatusCode.OK, createResponse.status)
            val getResponse = client.get("user/${user.id}")
            assertEquals(HttpStatusCode.OK, getResponse.status)
            val citation = Citation(
                UUID.randomUUID(),
                user.id!!,
                "http://falseclaim.com",
                "I doubt this claim"
            )
            val createCitationResponse = client.post("citation") {
                contentType(ContentType.Application.Json)
                setBody(citation)
            }
            assertEquals(HttpStatusCode.OK, createCitationResponse.status)
            val getCitationResponse = client.get("citation/${citation.id}")
            assertEquals(HttpStatusCode.OK, getCitationResponse.status)
        }
    }

    companion object {
        private val thread = Thread {
            val databaseConfig: DatabaseConfig = DatabaseConfig()
            val dataService: DataService = DataServiceMysqlImpl(databaseConfig)
            val serverConfig: ServerConfig = ServerConfig()
            val restService: RestService = RestService(serverConfig, dataService)
        }

        @JvmStatic
        @BeforeAll
        fun standupServer() {
            thread.start()
            Thread.sleep(Duration.ofSeconds(10))
        }
    }
}