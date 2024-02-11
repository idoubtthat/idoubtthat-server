package idoubtthat.pgp

import io.ktor.client.*
import io.ktor.client.engine.cio.*

class Keyservers {
    val client = HttpClient(CIO) {
        expectSuccess = true
    }

}