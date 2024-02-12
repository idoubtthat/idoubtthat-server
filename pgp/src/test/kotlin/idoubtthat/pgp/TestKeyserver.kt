package idoubtthat.pgp

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestKeyserver {

    @Test
    @Disabled("requires off-site http")
    fun testGetKeyByID() {
        val ks = Keyserver()
        val kid = PGPUtils.publicKeyFromArmoredString(TestData.publicKey).keyID
        val remoteKey = runBlocking {
            ks.get(kid)
        }
        assertEquals(kid, remoteKey.keyID)
    }

    @Test
    @Disabled("requires off-site http")
    fun testGetKeyByEmail() {
        val ks = Keyserver()
        val key = PGPUtils.publicKeyFromArmoredString(TestData.publicKey)
        val users = key.userIDs.asSequence().first()
        val email = PGPUtils.userEmail(users)
        val remoteKey = runBlocking {
            ks.getByEmail(email)
        }
        assertEquals(key.keyID, remoteKey.keyID)
    }
}
