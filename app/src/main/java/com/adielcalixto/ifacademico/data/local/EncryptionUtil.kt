package com.adielcalixto.ifacademico.data.local

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class EncryptionUtil {
    private val provider = "AndroidKeyStore"
    private val cipher by lazy { Cipher.getInstance("AES/GCM/NoPadding") }
    private val charset by lazy { charset("UTF-8") }

    private val keyGenerator by lazy {
        KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            provider
        )
    }

    private val keyStore by lazy {
        KeyStore.getInstance(provider).apply {
            load(null)
        }
    }

    fun encrypt(data: String, keyAlias: String): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(keyAlias))
        return cipher.iv + cipher.doFinal(data.toByteArray(charset))
    }

    fun decrypt(data: ByteArray, keyAlias: String): String {
        val dataWithoutIv = data.copyOfRange(12, data.size)
        val iv = data.copyOfRange(0, 12)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(keyAlias), GCMParameterSpec(128, iv))
        return cipher.doFinal(dataWithoutIv).toString(charset)
    }

    private fun getSecretKey(keyAlias: String): SecretKey {
        val existingKey = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createSecretKey(keyAlias)
    }

    private fun createSecretKey(keyAlias: String): SecretKey {
        return keyGenerator.apply {
            init(
                KeyGenParameterSpec
                    .Builder(
                        keyAlias,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            )
        }.generateKey()
    }
}