package idoubtthat.pgp

import idoubtthat.values.*
import org.bouncycastle.openpgp.PGPPublicKey
import org.bouncycastle.openpgp.PGPSecretKey
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PGPTest {
    @Test
    fun testKeygen() {
        val sec = PGPUtils.generate(TestData.user, TestData.passphrase)
        val s = PGPUtils.secretKeyToArmoredString(sec)
        val readSec = PGPUtils.secretKeyFromArmoredString(s)
        assertEquals(sec.secretKey.keyID, readSec.keyID)
        assertEquals(TestData.user, readSec.userIDs.next())
    }

    @Test
    fun testSignAndVerify() {
        val sec = PGPUtils.secretKeyFromArmoredString(TestData.testSecretKey)
        val message = "a test message to sign"
        val sig = PGPUtils.sign(message, sec, TestData.passphrase)
        val valid = PGPUtils.verify(sig, sec.publicKey, message)
        assertTrue(valid)
    }

    @Test
    fun testVerify() {
        val pubKey = PGPUtils.publicKeyFromArmoredString(TestData.publicKey)
        val sig = PGPUtils.readDetachedSignature(TestData.ascSigGic)
        assertEquals(pubKey.keyID, sig.keyID)
        val valid = PGPUtils.verify(sig, pubKey, TestData.text)
        assertTrue(valid)
        val sig2 = PGPUtils.readDetachedSignature(TestData.ascSigTom)
        assertEquals(pubKey.keyID, sig.keyID)
        val tomKey = PGPUtils.secretKeyFromArmoredString(TestData.testSecretKey).publicKey
        val valid2 = PGPUtils.verify(sig2, tomKey, TestData.text)
        assertTrue(valid2)
    }

    @Test
    fun testKeySign() {
        val tomKey = PGPUtils.secretKeyFromArmoredString(TestData.testSecretKey)
        val newPubKey = PGPUtils.generate("dave@idobutthat.info", "test").publicKey
        assertNotNull(newPubKey.userIDs.asSequence().firstOrNull())
        val initialSigs = newPubKey.signatures.asSequence().toList()
        assertEquals(1, initialSigs.size)
        assertTrue(initialSigs.any { it.keyID == newPubKey.keyID })
        assertFalse(initialSigs.any { it.keyID == tomKey.keyID })
        val signed = PGPUtils.signPublicKey(newPubKey, tomKey, TestData.passphrase)
        val newSigs =  signed.signatures.asSequence().toList()
        assertEquals(2, newSigs.size)
        assertTrue(newSigs.any { it.keyID == tomKey.keyID })
    }

    @Test
    fun testPGP() {
        val pgp = PGP(MemoryKeyStore())
        val sec = PGPUtils.generate(TestData.user, TestData.passphrase)
        pgp.keyStore.store(sec.secretKey)
        val resource = Resource(
            "This repo is dope",
            "git@github.com:idoubtthat/idoubtthat-server",
            Instant.now(),
            ResourceType.GITHUB
        )
        val statement = Signable(
            resource,
            Instant.now(),
            sec.publicKey.userIDs.next(),
            ValidityClaims(isTrue = true, isSupported = false, isGood = true)
        )
        val signed = pgp.sign(statement, TestData.passphrase)
        val isValid = pgp.isValid(signed)
        assertTrue(isValid)
    }

    @Test
    fun testEmailExtraction() {
        val key = PGPUtils.publicKeyFromArmoredString(TestData.publicKey)
        val userId = key.userIDs.next()
        val email = PGPUtils.userEmail(userId)
        assertEquals("grahamiancummins@gmail.com", email)
    }
}

class MemoryKeyStore(): KeyStore {
    val keys = mutableMapOf<Long, PGPPublicKey>()
    val secret = mutableMapOf<Long, PGPSecretKey>()
    override fun store(key: PGPPublicKey) {
        keys[key.keyID] = key
    }

    override fun store(key: PGPSecretKey) {
        secret[key.keyID] = key
        keys[key.keyID] = key.publicKey
    }

    override fun get(keyId: Long): PGPPublicKey? = keys[keyId]

    override fun getSecret(keyId: Long): PGPSecretKey? = secret[keyId]

    override fun getUser(userId: String): User? {
        val sec = secret.values.find { s -> s.userIDs.asSequence().any { it == userId } }
        val pubs = keys.values.filter { s -> s.userIDs.asSequence().any { it == userId } }
        if (pubs.isEmpty()) return null
        return User(userId, pubs.map { it.keyID }.toSet(), sec?.keyID)
    }
}
