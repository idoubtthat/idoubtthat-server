package idoubtthat.pgp

import idoubtthat.values.Serializer
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import org.bouncycastle.openpgp.PGPPublicKey
import java.net.URLEncoder

class Keyserver(private val server: String = "https://keys.openpgp.org/vks/v1/") {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson()
        }
        install(Logging)
        expectSuccess = true
        defaultRequest {
            url(server)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
        }
    }

    suspend fun get(id: Long): PGPPublicKey {
        val resp = client.get("by-keyid/${hexId(id)}")
        return PGPUtils.publicKeyFromArmoredString(resp.bodyAsText(Charsets.US_ASCII))
    }

    suspend fun put(key: PGPPublicKey) {
        client.post("upload") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("keytext" to PGPUtils.publicKeyToArmoredString(key)))
        }
    }

    /**
     * Email address must be a clean email address, not a key userId.
     * Use PGPUtils::userEmail to ensure this if needed
     */
    suspend fun getByEmail(email: String): PGPPublicKey {
        val urlEncodedEmail = URLEncoder.encode(email, Charsets.UTF_8)
        val url = Url("by-email/$urlEncodedEmail")
        val resp = client.get(url)
        return PGPUtils.publicKeyFromArmoredString(resp.bodyAsText(Charsets.US_ASCII))
    }

    companion object {
        fun hexId(id: Long): String = java.lang.Long.toHexString(id).uppercase()
    }
}
