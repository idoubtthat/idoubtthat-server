package idoubtthat.pgp

import idoubtthat.values.Signable
import idoubtthat.values.SignedReource
import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags.AES_256
import org.bouncycastle.openpgp.*
import org.bouncycastle.openpgp.operator.PGPDigestCalculator
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder
import java.io.*
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Security
import java.security.SignatureException
import java.util.*


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
    val digestCalculator: PGPDigestCalculator = BcPGPDigestCalculatorProvider().get(PGPUtil.SHA1)
    val keyEncryptorBuilder = BcPBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, digestCalculator)
    init {
        Security.addProvider(org.bouncycastle.jce.provider.BouncyCastleProvider())
    }

    fun generate(passphrase: String): PGPSecretKey {
        val kpGen = KeyPairGenerator.getInstance("DSA", "BC")
        kpGen.initialize(2048, SecureRandom())
        val kp = kpGen.generateKeyPair()
        val elgKp: PGPKeyPair = JcaPGPKeyPair(
            PGPPublicKey.DSA, kp, Date()
        )
        val keyEncryptor = keyEncryptorBuilder.build(passphrase.toCharArray())
        return PGPSecretKey(
            elgKp.privateKey,
            elgKp.publicKey,
            digestCalculator,
            false,
            keyEncryptor
        )
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
        TODO()
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

    fun secretKeyFromArmoredString(key: String): PGPSecretKey {
        val ins = PGPUtil.getDecoderStream(ByteArrayInputStream(key.toByteArray(Charsets.US_ASCII)))
        val pgpSec = PGPSecretKeyRing(ins, fingerprintCalculator)
        return pgpSec.secretKey
    }

    fun secretKeyToArmoredString(key: PGPSecretKey): String {
        val baos = ByteArrayOutputStream()
        val out = ArmoredOutputStream(baos)
        key.encode(out)
        out.close()
        return String(baos.toByteArray(), Charsets.US_ASCII)
    }

    fun sign(content: String, secretKey: PGPSecretKey, passphrase: String): PGPSignature {
        val decryptor = JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(passphrase.toCharArray())
        val privateKey = secretKey.extractPrivateKey(decryptor)
        val signatureGenerator = PGPSignatureGenerator(
            BcPGPContentSignerBuilder(
                secretKey.getPublicKey().getAlgorithm(),
                PGPUtil.SHA1
            )
        )
        signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey)
        signatureGenerator.update(content.toByteArray(Charsets.UTF_8))
        return signatureGenerator.generate()
    }
}


