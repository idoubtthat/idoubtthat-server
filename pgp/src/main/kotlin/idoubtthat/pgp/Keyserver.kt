package idoubtthat.pgp

import idoubtthat.values.Serializer
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.bouncycastle.openpgp.PGPPublicKey
import java.net.URLEncoder

class Keyserver(val server: String = "https://keys.openpgp.org") {
    val client = HttpClient(CIO) {
        expectSuccess = true
    }

    suspend fun get(id: Long): PGPPublicKey {
        val l = hexId(id)
        val resp = client.get("$server/vks/v1/by-keyid/$l")
        return PGPUtils.publicKeyFromArmoredString(resp.bodyAsText(Charsets.US_ASCII))
    }

    suspend fun put(key: PGPPublicKey) {
        client.post("$server/vks/v1/upload") {
            contentType(ContentType.Application.Json)
            setBody(Serializer.write(
                mapOf("keytext" to PGPUtils.publicKeyToArmoredString(key))
            ))
        }
    }

    suspend fun getByEmail(email: String): PGPPublicKey {
        val l = URLEncoder.encode(email, Charsets.UTF_8)
        val url = Url("$server/vks/v1/by-email/$l")
        val resp = client.get(url)
        return PGPUtils.publicKeyFromArmoredString(resp.bodyAsText(Charsets.US_ASCII))
    }

    companion object {
        fun hexId(id: Long): String = java.lang.Long.toHexString(id).uppercase()
    }
}
