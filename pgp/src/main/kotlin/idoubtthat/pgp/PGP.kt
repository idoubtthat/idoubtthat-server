package idoubtthat.pgp

import idoubtthat.values.Signable
import idoubtthat.values.SignedResource
import idoubtthat.values.User
import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.bcpg.HashAlgorithmTags
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags
import org.bouncycastle.bcpg.sig.KeyFlags
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters
import org.bouncycastle.openpgp.*
import org.bouncycastle.openpgp.operator.PGPDigestCalculator
import org.bouncycastle.openpgp.operator.bc.*
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder
import java.io.*
import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.security.SecureRandom
import java.security.Security
import java.security.SignatureException
import java.util.*


/**
 * Consider https://codeberg.org/PGPainless/pgpainless to reduce Bouncy parameters
 */

interface KeyStore {
    fun store(key: PGPPublicKey)
    fun store(key: PGPSecretKey)
    fun get(keyId: Long): PGPPublicKey?
    fun getSecret(keyId: Long): PGPSecretKey?
    fun getUser(userId: String): User?

    fun getSecret(userId: String): PGPSecretKey? {
        val secId = getUser(userId)?.onlineSecretKey ?: return null
        return getSecret(secId)
    }

    fun userOwnsKey(userId: String, keyId: Long): Boolean =
        getUser(userId)?.publicKeyIds?.contains(keyId) == true
}

class PGP(val keyStore: KeyStore) {
    fun isValid(signed: SignedResource): Boolean {
        val sig = PGPUtils.readDetachedSignature(signed.signature)
        if (!keyStore.userOwnsKey(signed.content.signedBy, sig.keyID)) {
            return false
        }
        val key = keyStore.get(sig.keyID) ?: throw Exception("Key ${sig.keyID} not found")
        return PGPUtils.verify(sig, key, signed.content.write())
    }

    fun sign(content: Signable, passphrase: String): SignedResource {
        val key = keyStore.getSecret(content.signedBy) ?: throw Exception("no secret key found")
        return sign(content, key, passphrase)
    }

    fun sign(content: Signable, key: PGPSecretKey, passphrase: String): SignedResource {
        val sig = PGPUtils.sign(content.write(), key, passphrase)
        return SignedResource(content, PGPUtils.writeDetachedSignature(sig))
    }
}

object PGPUtils {
    val fingerprintCalculator = BcKeyFingerprintCalculator()
    val digestCalculator: PGPDigestCalculator = BcPGPDigestCalculatorProvider().get(PGPUtil.SHA1)
    val keyEncryptorBuilder = BcPBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, digestCalculator)
    val signatureHashGen = PGPSignatureSubpacketGenerator().also {
        it.setKeyFlags(false, KeyFlags.SIGN_DATA or KeyFlags.CERTIFY_OTHER)
        it.setPreferredHashAlgorithms(false, intArrayOf(HashAlgorithmTags.SHA256, HashAlgorithmTags.SHA1))
        it.setPreferredSymmetricAlgorithms(false, intArrayOf(SymmetricKeyAlgorithmTags.AES_256))
    }
    val random = SecureRandom()

    init {
        Security.addProvider(org.bouncycastle.jce.provider.BouncyCastleProvider())
    }

    const val emailRegexString = "[a-zA-Z0-9+._%\\-]{1,256}" +
            "@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"


    val emailRegex = Regex(emailRegexString)
    val emailInIdRegex = Regex("<($emailRegexString)>\$")

    fun generate(
        id: String,
        passphrase: String,
        strength: Int = 2048
    ): PGPSecretKeyRing {
        val kpGen =  RSAKeyPairGenerator()
        kpGen.init(
            RSAKeyGenerationParameters(
                BigInteger.valueOf(0x10001),
                random, strength, 12
            )
        )
        val signKey: PGPKeyPair = BcPGPKeyPair(PGPPublicKey.RSA_SIGN, kpGen.generateKeyPair(), Date())

        val keyRingGen = PGPKeyRingGenerator(
            PGPSignature.POSITIVE_CERTIFICATION,
            signKey,
            id,
            digestCalculator,
            signatureHashGen.generate(),
            null,
            BcPGPContentSignerBuilder(signKey.publicKey.algorithm, HashAlgorithmTags.SHA1),
            keyEncryptorBuilder.build(passphrase.toCharArray())
        )
        return keyRingGen.generateSecretKeyRing()
    }

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

    fun writeDetachedSignature(sig: PGPSignature): String {
        val baos = ByteArrayOutputStream()
        val asc = ArmoredOutputStream(baos)
        sig.encode(asc)
        asc.close()
        return baos.toString(Charsets.US_ASCII)
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

    fun publicKeyToArmoredString(key: PGPPublicKey): String {
        val baos = ByteArrayOutputStream()
        val out = ArmoredOutputStream(baos)
        key.encode(out)
        out.close()
        return String(baos.toByteArray(), Charsets.US_ASCII)
    }

    fun secretKeyFromArmoredString(key: String): PGPSecretKey {
        val ins = PGPUtil.getDecoderStream(ByteArrayInputStream(key.toByteArray(Charsets.US_ASCII)))
        val pgpSec = PGPSecretKeyRing(ins, fingerprintCalculator)
        return pgpSec.secretKey
    }

    fun secretKeyToArmoredString(key: PGPSecretKeyRing): String {
        val baos = ByteArrayOutputStream()
        val out = ArmoredOutputStream(baos)
        key.encode(out)
        out.close()
        return String(baos.toByteArray(), Charsets.US_ASCII)
    }

    fun extractPrivateKey(secretKey: PGPSecretKey, passphrase: String): PGPPrivateKey {
        val decryptor = JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(passphrase.toCharArray())
        return secretKey.extractPrivateKey(decryptor)
    }

    fun sigGenerator(secretKey: PGPSecretKey): PGPSignatureGenerator = PGPSignatureGenerator(
        BcPGPContentSignerBuilder(secretKey.publicKey.algorithm, PGPUtil.SHA256)
    )

    fun sign(content: String, secretKey: PGPSecretKey, passphrase: String): PGPSignature {
        val privateKey = extractPrivateKey(secretKey, passphrase)
        val signatureGenerator = sigGenerator(secretKey)
        signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey)
        signatureGenerator.update(content.toByteArray(Charsets.UTF_8))
        return signatureGenerator.generate()
    }

    fun signPublicKey(toSign: PGPPublicKey, secretKey: PGPSecretKey, passphrase: String): PGPPublicKey {
        val privateKey = extractPrivateKey(secretKey, passphrase)
        val signatureGenerator = sigGenerator(secretKey)
        signatureGenerator.init(PGPSignature.DEFAULT_CERTIFICATION, privateKey)
        val id = toSign.getUserIDs().asSequence().firstOrNull() ?: throw Exception("No ID on target key")
        val signature = signatureGenerator.generateCertification(id, toSign)
        return PGPPublicKey.addCertification(toSign, id, signature)
    }

    fun userEmail(userId: String): String {
        val stdId = emailInIdRegex.find(userId)
        if (stdId != null) {
            return stdId.groups[1]!!.value
        }

        val bareEmail = emailRegex.matchEntire(userId)
        if (bareEmail != null) {
            return userId
        }
        throw IllegalArgumentException("No email")
    }
}

