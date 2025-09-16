package com.example.faceapp.utils

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

@RequiresApi(Build.VERSION_CODES.M)
class CryptoManager {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
        init(Cipher.ENCRYPT_MODE,getKey())
    }
    private fun decryptCipher(iv : ByteArray) : Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE,getKey(),IvParameterSpec(iv))
        }
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry("secret",null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    //to generate our secretKey
    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    "secret",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    fun encrypt(bytes: ByteArray): Pair<String,String> {
        val encryptedBytes = Base64.getEncoder().encodeToString(encryptCipher.doFinal(bytes))
        val iv = Base64.getEncoder().encodeToString(encryptCipher.iv)
        return Pair(encryptedBytes,iv)
    }
    fun decrypt(pair: Pair<String, String>): ByteArray {
        val encryptedBytes = Base64.getDecoder().decode(pair.first)
        val iv = Base64.getDecoder().decode(pair.second)
        return decryptCipher(iv).doFinal(encryptedBytes)
    }

    //Cipher tells us about algorithms,padding and so more
    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }
}
