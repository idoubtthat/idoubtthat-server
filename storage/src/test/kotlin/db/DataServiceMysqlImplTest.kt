package db

import db.models.CitationDAO
import db.models.UserDAO
import info.idoubtthat.db.DataService
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DataServiceMysqlImplTest {
    private val databaseConfig: DatabaseConfig = DatabaseConfig()
    private val dataService: DataService = DataServiceMysqlImpl(databaseConfig)

    @Test
    fun testUserCRUD() {
        val user = UserDAO(
            UUID.randomUUID(),
            "Sage",
            "Smith"
        )
        assertNull(dataService.getUser(user))
        dataService.createUser(user)
        assertEquals(user, dataService.getUser(UserDAO(user.id)))
    }

    @Test
    @Disabled("This won't pass until the indexes are fixed")
    fun testCitationCRUD() {
        val user = UserDAO(
            UUID.randomUUID(),
            "Sage",
            "Smith"
        )
        assertNull(dataService.getUser(user))
        dataService.createUser(user)
        assertEquals(user, dataService.getUser(UserDAO(user.id)))
        val citation = CitationDAO(
            UUID.randomUUID(),
            user.id,
            "http://falseclaim.com",
            "I doubt this claim"
        )
        assertNull(dataService.getCitation(citation))
        dataService.createCitation(citation)
        assertEquals(citation, dataService.getCitation(CitationDAO(citation.id)))
    }
}