package db

import info.idoubtthat.api.DefaultApi
import info.idoubtthat.model.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ApiIntegrationTest {
    val api = DefaultApi()

    @Test
    fun testUser() {
        val user = User(
            firstName = "Sage",
            lastName = "Smith"
        )
        val id = api.apiV1UserPost(user)
        val fetchedUser = api.apiV1UserIdGet(id.toString())
        assertEquals(id, fetchedUser.id)
        assertEquals(user.firstName, fetchedUser.firstName)
        assertEquals(user.lastName, fetchedUser.lastName)
    }
}