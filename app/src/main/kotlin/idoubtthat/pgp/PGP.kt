package idoubtthat.pgp

import idoubtthat.values.Signable
import idoubtthat.values.SignedReource
import org.bouncycastle.asn1.x509.ObjectDigestInfo.publicKey
import org.bouncycastle.openpgp.*
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider
import java.io.*
import java.security.SignatureException


interface KeyStore {
    fun getUser(keyId: String): String?

    fun getKeyIdForUser(userId: String): String?
    fun getKey(userId: String): PGPPublicKey?
}

interface PGP {
    fun isValid(signed: SignedReource): Boolean
    fun sign(content: Signable, key: PGPPrivateKey): ByteArray
}


object PGPUtils {
    val fingerprintCalculator = BcKeyFingerprintCalculator()

    fun readDetachedSignature(sig: String): PGPSignature {
        val pgpObject = PGPUtil.getDecoderStream(ByteArrayInputStream(sig.toByteArray(Charsets.US_ASCII)))
            .use { inputStream ->
                PGPObjectFactory(inputStream, fingerprintCalculator).asSequence().firstOrNull() ?:
                throw SignatureException("Could not find signature")
            }
        return when (pgpObject) {
            is PGPSignature -> pgpObject
            is PGPSignatureList -> pgpObject.firstOrNull() ?: throw SignatureException("Could not find signature")
            else -> throw SignatureException("Could not find signature")
        }
    }

    fun verify(pgpSignature: PGPSignature, publicKey: PGPPublicKey, content: String): Boolean {
        pgpSignature.init(BcPGPContentVerifierBuilderProvider(), publicKey)
        DataInputStream(ByteArrayInputStream(content.toByteArray(Charsets.UTF_8))).use {
            pgpSignature.update(it.readAllBytes())
        }
        return pgpSignature.verify()
    }

    fun publicKeyFromArmoredString(key: String): PGPPublicKey {
        val ins = PGPUtil.getDecoderStream(ByteArrayInputStream(key.toByteArray(Charsets.US_ASCII)))
        val pgpPub = PGPPublicKeyRing(ins, fingerprintCalculator)
        return pgpPub.publicKey
    }

}


