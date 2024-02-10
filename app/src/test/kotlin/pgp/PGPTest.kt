package pgp

import idoubtthat.pgp.KeyStore
import idoubtthat.pgp.PGP
import idoubtthat.pgp.PGPUtils
import idoubtthat.values.*
import org.bouncycastle.openpgp.PGPPublicKey
import org.bouncycastle.openpgp.PGPSecretKey
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
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
        val valid2 = PGPUtils.verify(sig, pubKey, TestData.text)
        assertTrue(valid2)
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

object TestData {
    val user = "Tom Doubter <i@doubtthat.info>"
    val passphrase = "i doubt that"


    val text = "some text\n"
    val ascSigGic = """
        -----BEGIN PGP SIGNATURE-----

        iQIzBAABCAAdFiEEaP6XFSNMAgpwE+A8jL9PmNg8/IoFAmXBtDYACgkQjL9PmNg8
        /IrxDBAAyDoYVhbchcj59ERzZTcPngkEu9CONy3/Gj8IOyup5BPtMX9gosrNGRFr
        YBu+Qj7cyKAOUUL5MQdJLB1YnmDmCAI13yWSErft8MYfIkD+ndl+kSNh2oH651me
        W4O1XKxplw2Zw/FDNaM/l26lOGltq7lYTM/j7VGt9oOBjNwITlS4HETQQnLs1UY/
        jkwDAQ4ALlWsustS4tIXNkcb3QT5+ZnyAeEgnOa2ZhyuWZF/upocQbt4pQHiSTfK
        cxw5zkRtef0ktNgpL8JWrLtGpvTjtlRSl95ms7EC7QvjNxpmfy99dD0vFejG8TyD
        kvAPOZ6O/WclkTOX03s5at1Mqu7Tl+I5O1Iuj1tLbgRL7CVqwUc99xUN7uIEIY4k
        6PHB7lQFGCkcjHbIhMrXUHblaAacJwMT9OsE3Ite6YVtJ+sAwHzjA7ZqN9iKztt4
        MKDctaJwuyIjKBM4T3iSEHRqATC1VLmLKZ2s9AtJ1k3Xh4Z8WBCqE2PQO2AtxJpt
        Zkl17AIaiU6VV06Nkl9uqKMNwRDpIuxJMdE9nKJykDcuzJMh0B+l+gYIiVWzi+yx
        UTKTlDNYal9rzR9wL4pMiTA4TgZb9rbIYj9XlExFvhPeyuVbnCxWdjW/5wN9ty29
        g/7z3eq6SjjQh56+p/E3eC+uglCv9KaDx3y2Xg9axLhmExEMZ7s=
        =2Xbm
        -----END PGP SIGNATURE----
    """.trimIndent()

    val ascSigTom = """
        -----BEGIN PGP SIGNATURE-----

        iQEzBAADCgAdFiEEo891PL+bKkv+wyUr9aPROg6S0e4FAmXH3WkACgkQ9aPROg6S
        0e4tjwf+MzQt+VhiwEqCnJGaanbTOEQWrcgO6vd8cYAc3CZBgM8lUaWVB4YvSpgx
        132uFncRYJ4u3l+ANttv9Hb6f9lOaYDJBjbL5cvfCYWC1PfGRg0ECnwW+7Q82jPx
        wgeMTkg3PN3USbX0D/vSGd0dzyqtQnuULXAzpxbE0VIwDbrwwSEqml5drAtpqd8N
        CJM2ow/y1nvD8B+0s39pSkqLaMFFoh42sJiONVaKb8UepKzDm6gjnqONX2ar0bwT
        3QhZ+cqvbRiqDrE8OaVrI6POz9Mt5FDMey+q18/Es5bfmGK1KHZk0eTtR5k2vkCL
        sC/9TITbI1R0pZMi89Nse9YJ6CxNxA==
        =JAsI
        -----END PGP SIGNATURE-----
    """.trimIndent()

    val publicKey = """
      -----BEGIN PGP PUBLIC KEY BLOCK-----

      mQINBFoRwzMBEADT+1ICuohaFjk1NjYkDxUq+it4z4QDQcL6J8roralxfwI1CYOu
      CRBEbThvl7LD2uiwr0ohtLIVxUDrx0c3QHDSkfh7xwdpgP2LQj7OmvS+xfDo1FyZ
      JA79IbYDbjNpksCBtVQUia8Nl3VgO2ccC6ek5PAhwfKRYUKhQnVmxvTr7sOnAtKn
      +VUV3GLejXBJ0IP7NyZdauYpafCTQcZ6hYjDJQ0J8k4RocJqaUI8/UzJxCDb7iky
      yjfFytchx0XPSAKm2LLCg9m8fqUZ9wVuCCPO0UdnRX/NnOAVrt8o0MF5l/7lmzFH
      MnMIlIjpdsADI3msxDcbPPvhgIa9XPkmCduVaT9wg7OGbxENVJdVv76ycX2pdL6Z
      ZRgqPSBXEyXTcwVFgtUrWPilbjeET+x7RxpVn7mxVPVhXbJnXWbAYkqMFDfk3nRY
      lCIRVeBtW2SbQB2KX9B9l2XCFWRI6F9KfV5XxlUGPHqIsuSvZENybm1KCF09krkz
      ObhMDGUb58X3LLhkVjGhHh5W3ECCJ0jc7b5+GTTgviucS49P6dpw4l9KuruHc00E
      oxDxS0DyW1A/+ZX9vqmh/fI0WaSUa0SQzSHTjARasxamuwWA2x0JqO3DfNgog/Kw
      8tmuGpIY0H4NIFCETa4AICz4zpfHyAvyQRjZbpTseHuON0Wp+5N6mnYQ3wARAQAB
      tJdHcmFoYW0gQ3VtbWlucyAoU2FtZSBpZGVudGl0eSBhcyBrZXkgRDIzMkQxODYy
      RDA3QjMwMkEwRTk2RkZFOUI4M0Y5Mjg0REFFN0QwQSBmb3IgZ2ljQGxvcmF4Lm9y
      Zy4gTmV3IGtleSBjcmVhdGVkIDExLzE5LzE3KSA8Z3JhaGFtaWFuY3VtbWluc0Bn
      bWFpbC5jb20+iQJOBBMBCAA4FiEEaP6XFSNMAgpwE+A8jL9PmNg8/IoFAloRwzMC
      GwMFCwkIBwIGFQgJCgsCBBYCAwECHgECF4AACgkQjL9PmNg8/Ir0nQ/+PH4XXIAj
      Lp8oJR7nznALnWgV2gv9OjAerhssIYwQR+4BLOFzOKIZ+UHyKl3TvXK7hXpkFVzz
      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
      =Mgkw
      -----END PGP PUBLIC KEY BLOCK-----
    """.trimIndent()

    val testSecretKey = """
    -----BEGIN PGP PRIVATE KEY BLOCK-----
    Version: BCPG v1.77.00
    
    lQPGBGXHqKEDCACvySnPZiA3X9790jUE155BbqZmAWW9oe+RDTm3RiRrbd9iE7ba
    d0jcGVmpokQcnR0oVw7tsl/ZLuHoKn8FNm3VJwoJkuY1bqqHa75ftuIU9qV4PZ5o
    VueHcfyhDJrOoMnk2NyZn0Pcshpbc1CAjhMA6V3rfO5OwEd9WmkZn3kybHdQtG1Z
    F4KnRSJ29SDoM9hPhj1k7sQsPL9Mi1J8xshvJXr5RBeQ+vWXNO7DLaAMOmCRisZd
    i3HgysEHFYMCeIi2ngJg5g6cE+AspOuRgoUTwyzusDuZTlwAmBErrQ0lR6jAAThp
    BF4HN1PecgE1UJO8DNVCkmCJK84nzydl//SJABEBAAH+CQMC5Tk1673lcbhgcJUa
    am4yUPWpJlbd0KxoQ/dO/9WkOrUi8Xq9mb6vExe+t5uW/NZImz1Z/VXDZm3RafCf
    TIRxlxlMAsqcsYiIICUm/oHdd0wVHE8ecUy9G0WdXYgRFZwHbnbz3D8IZ01AIfLF
    RkkwBC5vyZ97yhoXgMv+V1PPpnd86kkcXzimGxFTlOmTFdMr+CtjrcU6rRr8hdis
    6JIgOAmHvODb+ApHsb1ESHPQ2wiezJY9jLiQ3ryRGJyWbUSw7xVsCt9Z4X7oJqxw
    NknpcYPzGWHhx3q5K0k1912V3ppP5YBAjGvdH9yQdRQfsKY8uqX3duHkMZNKBsqz
    sKXHWJfybT0WEfE6MZahAx9Kxldp5nCmVTkAP8GOcOdoXECcQaY8uD3iIJ/ggNrV
    l94t8Uh2tz9USaWS7uffA32903KoFRvuhbEJ6ox2gW3YUSguJftjXc2YFLWTvTIL
    GTF65b3fzfbKQT02mBXH4kNqOQCbUXZQnGysYQBNcox+zhSkCbx6kl0TS1kUUVtD
    SEQXiCDzBXRnD06nOg+8hxilXUqlefpnIl1UzJSP56hFe0/guuuhr128GryJ/ciH
    VbgBa6NYfxOtbeIQMedQ+tL3R5fCkebIDplwQjHACT/pD2yrWU8rqgT8AOJKdHkb
    celaM9/abWdbwxK4rj8TalTAZYKxb8lv1Bzc5LUZRvoTB9DauyO8s5BlH1L1yHHT
    EI8y61JmMgWJ9hUusz+OcJyXRr7tVC011Ma5MfvzW3VuUidC/0XAD8ptKHlmDjTf
    /o8ZC7pi9yorIpkvVdRRn9SxSixNIKw/s1g3MaMW6kQIS8ju2gs7l0vmnF4llCJ3
    HnjRHxxEx8YSAo+seOzvfp97vP3P+vV7XtAbT7ggArlnJIZ1G4bc+6TNG34j4crg
    jOxaMMOrxA3ZtB5Ub20gRG91YnRlciA8aUBkb3VidHRoYXQuaW5mbz6JASYEEwMC
    ABAFAmXHqKECGwMDFQgCAgsJAAoJEPWj0ToOktHuSyYH/2x/qJ1h1S67jFzV8w2q
    D2DDluDEl21pLUGTDY7PECluqOFGlXPoM1ttaGeDIYLKPdQOLpjM2Tpck+WO6Dn4
    NlnCDWN99/HZSv7eDPy6AfoXERdpYynQeddqTHnAFprHWXhhRpF6yXeEUt5Xraat
    05c/FR9rED7MM8pjl1Y+7rOP9cV9Yrr7naUScesRLMRFpDgieQjlAo7j8e8gJ+uh
    R3fQOeHe3QPw7exsgMgMMCfdbPdxY1sjGUbdCrlXtfySLNllSGb8kTWBx4U/IhR1
    ujHDl1rXCKkFifCHfLy7jVUt1wingqurWosIMZgqJquM244s/n0dPr/+TzgTp+cN
    V1A=
    =CAme
    -----END PGP PRIVATE KEY BLOCK-----
    """.trimIndent()
       

}