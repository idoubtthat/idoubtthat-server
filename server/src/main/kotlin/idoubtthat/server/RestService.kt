package idoubtthat.server

import com.fasterxml.jackson.databind.SerializationFeature
import db.models.CitationDAO
import db.models.UserDAO
import info.idoubtthat.db.DataService
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.util.*

class RestService(
    serverConfig: ServerConfig,
    dataService: DataService
) {
    private val logger: Logger = LoggerFactory.getLogger(RestService::class.java)
    private val server = embeddedServer(Netty, port = serverConfig.port, host = serverConfig.host) {
        install(CallLogging) {
            level = Level.INFO
            filter { call -> call.request.path().startsWith("/") }
        }
        install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
            }
        }
        install(CORS) {
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowMethod(HttpMethod.Patch)
            allowHeader(HttpHeaders.Authorization)
            anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
        }

        routing {
            swaggerUI(path = "api/v1/swagger")
            openAPI(path = "api/v1/openapi")

            route("/api/v1") {
                route("/user") {
                    post {
                        val user = call.receive<User>()
                        val id = user.id ?: UUID.randomUUID()
                        dataService.createUser(
                            UserDAO(
                                id = id,
                                firstName = user.lastName,
                                lastName = user.firstName
                            )
                        )
                        call.respond(id)
                    }

                    get ("/{id}") {
                        val requestedUser = UserDAO(
                            UUID.fromString(call.parameters["id"]),
                            call.parameters["first_name"],
                            call.parameters["last_name"]
                        )
                        val fetchedUser = dataService.getUser(requestedUser)
                        if (fetchedUser != null) {
                            call.respond(User(fetchedUser.id!!, fetchedUser.firstName!!, fetchedUser.lastName!!))
                        }
                    }

                }
                route("/citation") {
                    post {
                        val citation = call.receive<Citation>()
                        val id = citation.id ?: UUID.randomUUID()
                        dataService.createCitation(
                            CitationDAO(
                                id = id,
                                userId = citation.author,
                                url = citation.url,
                                commentary = citation.commentary
                            )
                        )
                        call.respond(id)
                    }

                    get("/{id}") {
                        val requestedCitation = CitationDAO(
                            UUID.fromString(call.parameters["id"]),
                            if (call.parameters.contains("author")) UUID.fromString(call.parameters["user_id"]) else null,
                            call.parameters["url"],
                            call.parameters["commentary"]
                        )
                        val fetchedCitation = dataService.getCitation(requestedCitation)
                        if (fetchedCitation != null) {
                            call.respond(Citation(fetchedCitation.id!!, fetchedCitation.userId!!, fetchedCitation.url!!, fetchedCitation.commentary!!))
                        }
                    }
                }
            }
        }
    }

    init {
        server.start(wait = true)
    }
}

data class User(
    val id: UUID?,
    val firstName: String,
    val lastName: String
)

data class Citation(
    val id: UUID?,
    val author: UUID,
    val url: String,
    val commentary: String
)
