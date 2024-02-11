package db

import info.idoubtthat.db.DataServiceMysqlImpl
import info.idoubtthat.db.DatabaseManager
import info.idoubtthat.models.CitationDAO
import info.idoubtthat.models.UserDAO
import info.idoubtthat.server.Environment
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.UUID


class DataServiceMysqlImplTest {
    private val databaseManager = DatabaseManager(Environment())
    val dataServiceMysqlImpl = DataServiceMysqlImpl(databaseManager)


    @Test
    @Disabled("Will fix once dataservice does something again")
    @Throws(Exception::class)
    fun testUser() {
        databaseManager.migrate()
        val id = UUID.randomUUID()
        val first = "Sage"
        val last = "Smith"
        dataServiceMysqlImpl.createUser(
            UserDAO(
                id,
                first,
                last
            )
        )
        val user = dataServiceMysqlImpl.getUser(UserDAO(id))
        println(user)
        assertEquals(id, user?.id)
        assertEquals(first, user?.firstName)
        assertEquals(last, user?.lastName)
    }

    @Test
    @Disabled("Fix once dataservice works again")
    @Throws(Exception::class)
    fun testCitation() {
        databaseManager.migrate()
        val userId = UUID.randomUUID()
        val first = "Sage"
        val last = "Smith"
        dataServiceMysqlImpl.createUser(
            UserDAO(
                userId,
                first,
                last
            )
        )
        val citationId = UUID.randomUUID()
        val url = "http://url.com"
        val commentary = "Yes, I have opinions"
        dataServiceMysqlImpl.createCitation(
            CitationDAO(
                id = citationId,
                url = url,
                commentary = commentary,
                userId = userId
            )
        )

        val citationDAO = dataServiceMysqlImpl.getCitation(CitationDAO(citationId))
        println(citationDAO)
        assertEquals(citationDAO?.id, citationId)
        assertEquals(citationDAO?.url, url)
        assertEquals(citationDAO?.userId, userId)
        assertEquals(citationDAO?.commentary, commentary)

        val newCommentary = "New commentary"
        dataServiceMysqlImpl.updateCitation(
            CitationDAO(
                id = citationId,
                url = url,
                commentary = newCommentary,
                userId = userId
            )
        )
    }
}