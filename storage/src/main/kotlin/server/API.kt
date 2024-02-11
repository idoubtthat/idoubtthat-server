package info.idoubtthat.server

import info.idoubtthat.api.DefaultApi
import info.idoubtthat.db.DataService
import info.idoubtthat.model.Citation
import info.idoubtthat.model.User
import info.idoubtthat.models.CitationDAO
import info.idoubtthat.models.UserDAO
import java.util.*
import javax.ws.rs.NotFoundException


class API(private val dataService: DataService): DefaultApi {
    override fun apiV1CitationIdGet(id: String): Citation {
        val citationDAO = dataService.getCitation(CitationDAO(UUID.fromString(id)))
        return if (citationDAO != null) {
            Citation(
                id = citationDAO.id,
                author = citationDAO.userId,
                url = citationDAO.url,
                commentary = citationDAO.commentary
            )
        } else {
            throw NotFoundException()
        }
    }

    override fun apiV1CitationPost(citation: Citation): UUID {
        val id = UUID.randomUUID()
        dataService.createCitation(
            CitationDAO(
                id,
                citation.author,
                citation.url,
                citation.commentary
            )
        )
        return id
    }

    override fun apiV1UserIdGet(id: String): User {
        val userDAO = dataService.getUser(UserDAO(UUID.fromString(id)))
        return if (userDAO != null) {
            User(
                id = userDAO.id,
                firstName = userDAO.firstName,
                lastName = userDAO.lastName
            )
        } else {
            throw NotFoundException()
        }
    }

    override fun apiV1UserPost(user: User): UUID {
        val id = UUID.randomUUID()
        dataService.createUser(
            UserDAO(
                id,
                user.firstName,
                user.lastName
            )
        )
        return id
    }
}