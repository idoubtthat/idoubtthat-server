package idoubtthat.pgp

import org.bouncycastle.asn1.ASN1ObjectIdentifier
import org.bouncycastle.asn1.cryptlib.CryptlibObjectIdentifiers
import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.bcpg.HashAlgorithmTags
import org.bouncycastle.bcpg.PublicKeyAlgorithmTags.ECDH
import org.bouncycastle.bcpg.PublicKeyAlgorithmTags.ECDSA
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags.*
import org.bouncycastle.bcpg.sig.Features
import org.bouncycastle.bcpg.sig.KeyFlags
import org.bouncycastle.bcpg.sig.KeyFlags.*
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.ec.CustomNamedCurves
import org.bouncycastle.crypto.generators.ECKeyPairGenerator
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator
import org.bouncycastle.crypto.params.ECKeyGenerationParameters
import org.bouncycastle.crypto.params.ECNamedDomainParameters
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters
import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.*
import org.bouncycastle.openpgp.PGPException
import org.bouncycastle.openpgp.PGPSignature.POSITIVE_CERTIFICATION
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider
import org.bouncycastle.openpgp.operator.bc.BcPGPKeyPair
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.InvalidAlgorithmParameterException
import java.security.Provider
import java.security.SecureRandom
import java.util.*


object RSAGen {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val pass = charArrayOf('h', 'e', 'l', 'l', 'o')
        val krgen = generateKeyRingGenerator("alice@example.com", pass)

        // Generate public key ring, dump to file.
        val pkr = krgen.generatePublicKeyRing()
        val pubout = ArmoredOutputStream(BufferedOutputStream(FileOutputStream("/home/user/dummy.asc")))
        pkr.encode(pubout)
        pubout.close()

        // Generate private key, dump to file.
        val skr = krgen.generateSecretKeyRing()
        val secout = BufferedOutputStream(FileOutputStream("/home/user/dummy.skr"))
        skr.encode(secout)
        secout.close()
    }

    // Note: s2kcount is a number between 0 and 0xff that controls the number of times to iterate the password hash before use. More
    // iterations are useful against offline attacks, as it takes more time to check each password. The actual number of iterations is
    // rather complex, and also depends on the hash function in use. Refer to Section 3.7.1.3 in rfc4880.txt. Bigger numbers give
    // you more iterations.  As a rough rule of thumb, when using SHA256 as the hashing function, 0x10 gives you about 64
    // iterations, 0x20 about 128, 0x30 about 256 and so on till 0xf0, or about 1 million iterations. The maximum you can go to is
    // 0xff, or about 2 million iterations.  I'll use 0xc0 as a default -- about 130,000 iterations.
    @JvmOverloads
    @Throws(Exception::class)
    fun generateKeyRingGenerator(id: String?, pass: CharArray?, s2kcount: Int = 0xc0): PGPKeyRingGenerator {
        // This object generates individual key-pairs.
        val kpg = RSAKeyPairGenerator()

        // Boilerplate RSA parameters, no need to change anything
        // except for the RSA key-size (2048). You can use whatever key-size makes sense for you -- 4096, etc.
        kpg.init(RSAKeyGenerationParameters(BigInteger.valueOf(0x10001), SecureRandom(), 2048, 12))

        // First create the master (signing) key with the generator.
        val rsakp_sign: PGPKeyPair = BcPGPKeyPair(PGPPublicKey.RSA_SIGN, kpg.generateKeyPair(), Date())
        // Then an encryption subkey.
        val rsakp_enc: PGPKeyPair = BcPGPKeyPair(PGPPublicKey.RSA_ENCRYPT, kpg.generateKeyPair(), Date())

        // Add a self-signature on the id
        val signhashgen = PGPSignatureSubpacketGenerator()

        // Add signed metadata on the signature.
        // 1) Declare its purpose
        signhashgen.setKeyFlags(false, KeyFlags.SIGN_DATA or KeyFlags.CERTIFY_OTHER)
        // 2) Set preferences for secondary crypto algorithms to use when sending messages to this key.
        signhashgen.setPreferredSymmetricAlgorithms(
            false, intArrayOf(
                SymmetricKeyAlgorithmTags.AES_256,
                SymmetricKeyAlgorithmTags.AES_192,
                SymmetricKeyAlgorithmTags.AES_128
            )
        )
        signhashgen.setPreferredHashAlgorithms(
            false, intArrayOf(
                HashAlgorithmTags.SHA256,
                HashAlgorithmTags.SHA1,
                HashAlgorithmTags.SHA384,
                HashAlgorithmTags.SHA512,
                HashAlgorithmTags.SHA224
            )
        )
        // 3) Request senders add additional checksums to the message (useful when verifying unsigned messages.)
        signhashgen.setFeature(false, Features.FEATURE_MODIFICATION_DETECTION)

        // Create a signature on the encryption subkey.
        val enchashgen = PGPSignatureSubpacketGenerator()
        // Add metadata to declare its purpose
        enchashgen.setKeyFlags(false, KeyFlags.ENCRYPT_COMMS or KeyFlags.ENCRYPT_STORAGE)

        // Objects used to encrypt the secret key.
        val sha1Calc = BcPGPDigestCalculatorProvider()[HashAlgorithmTags.SHA1]
        val sha256Calc = BcPGPDigestCalculatorProvider()[HashAlgorithmTags.SHA256]

        // bcpg 1.48 exposes this API that includes s2kcount. Earlier versions use a default of 0x60.
        val pske = BcPBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, sha256Calc, s2kcount)
            .build(pass)

        // Finally, create the keyring itself. The constructor takes parameters that allow it to generate the self signature.
        val keyRingGen = PGPKeyRingGenerator(
            PGPSignature.POSITIVE_CERTIFICATION, rsakp_sign,
            id, sha1Calc, signhashgen.generate(), null,
            BcPGPContentSignerBuilder(rsakp_sign.publicKey.algorithm, HashAlgorithmTags.SHA1), pske
        )

        // Add our encryption subkey, together with its signature.
        keyRingGen.addSubKey(rsakp_enc, enchashgen.generate(), null)
        return keyRingGen
    }
}


private val BOUNCY_CASTLE_PROVIDER: Provider = BouncyCastleProvider()
private val CURVE_OID = CryptlibObjectIdentifiers.curvey25519

private val MASTER_KEY_ALGORITHM: Int = ECDSA
private val SUB_KEY_ALGORITHM: Int = ECDH

private const val MASTER_KEY_FLAGS = AUTHENTICATION or CERTIFY_OTHER or SIGN_DATA or ENCRYPT_STORAGE or ENCRYPT_COMMS
private const val SUB_KEY_FLAGS = ENCRYPT_COMMS or ENCRYPT_STORAGE

private val PREFERRED_HASH_ALGORITHMS = intArrayOf(PGPUtil.SHA256, PGPUtil.SHA1, PGPUtil.SHA384, PGPUtil.SHA512, PGPUtil.SHA224)
private val PREFERRED_SYMMETRIC_ALGORITHMS = intArrayOf(AES_256, AES_192, AES_128)

@Throws(PGPException::class, InvalidAlgorithmParameterException::class)
fun createPGPKeyRingGenerator(identity: String?, passphrase: String, keySize: Int): PGPKeyRingGenerator {
    val masterKeyPair: PGPKeyPair = generateEcPgpKeyPair(MASTER_KEY_ALGORITHM)
    val subKeyPair: PGPKeyPair = generateEcPgpKeyPair(SUB_KEY_ALGORITHM)
    val sha1Calc = BcPGPDigestCalculatorProvider()[PGPUtil.SHA1]
    val sha256Calc = BcPGPDigestCalculatorProvider()[PGPUtil.SHA256]
    val masterKeySubPacket = generateMasterkeySubpacket(MASTER_KEY_FLAGS)
    val subKeySubPacket = generateSubkeySubpacket(SUB_KEY_FLAGS)
    val keyRingGenerator = PGPKeyRingGenerator(
        POSITIVE_CERTIFICATION, masterKeyPair, identity, sha1Calc, masterKeySubPacket,
        null,
        JcaPGPContentSignerBuilder(masterKeyPair.publicKey.algorithm, PGPUtil.SHA256)
            .setProvider(BOUNCY_CASTLE_PROVIDER),
        JcePBESecretKeyEncryptorBuilder(AES_256, sha256Calc)
            .setProvider(BOUNCY_CASTLE_PROVIDER)
            .build(passphrase.toCharArray())
    )
    keyRingGenerator.addSubKey(subKeyPair, subKeySubPacket, null)
    return keyRingGenerator
}

private fun generateEcKeyPair(curveOid: ASN1ObjectIdentifier): AsymmetricCipherKeyPair {
    val curve = CustomNamedCurves.getByOID(curveOid)
    val ecDomainParameters = ECNamedDomainParameters(curveOid, curve.curve, curve.g, curve.n, curve.h, curve.seed)
    val keyPairGenerator = ECKeyPairGenerator()
    keyPairGenerator.init(ECKeyGenerationParameters(ecDomainParameters, SecureRandom()))
    return keyPairGenerator.generateKeyPair()
}

@Throws(InvalidAlgorithmParameterException::class, PGPException::class)
private fun generateEcPgpKeyPair(algorithm: Int): BcPGPKeyPair {
    return BcPGPKeyPair(algorithm, generateEcKeyPair(CURVE_OID), Date())
}

private fun generateMasterkeySubpacket(keyFlags: Int): PGPSignatureSubpacketVector {
    val subpacketGen = PGPSignatureSubpacketGenerator()
    subpacketGen.setKeyFlags(false, keyFlags)
    subpacketGen.setPreferredSymmetricAlgorithms(false, PREFERRED_SYMMETRIC_ALGORITHMS)
    subpacketGen.setPreferredHashAlgorithms(false, PREFERRED_HASH_ALGORITHMS)
    return subpacketGen.generate()
}

private fun generateSubkeySubpacket(keyFlags: Int): PGPSignatureSubpacketVector {
    val subpacketGen = PGPSignatureSubpacketGenerator()
    subpacketGen.setKeyFlags(false, keyFlags)
    return subpacketGen.generate()
}