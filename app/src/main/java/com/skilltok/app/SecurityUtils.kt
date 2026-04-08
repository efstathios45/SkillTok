package com.skilltok.app

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom

object SecurityUtils {
    private const val PREFS_NAME = "secure_skilltok_prefs"
    private const val DB_PASSPHRASE_KEY = "db_passphrase"

    private fun getEncryptedPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getDatabasePassphrase(context: Context): ByteArray {
        val prefs = getEncryptedPrefs(context)
        var passphrase = prefs.getString(DB_PASSPHRASE_KEY, null)
        
        if (passphrase == null) {
            // Generate a unique random 256-bit passphrase for this device
            val random = SecureRandom()
            val bytes = ByteArray(32)
            random.nextBytes(bytes)
            // Use NO_WRAP to avoid trailing newlines
            passphrase = Base64.encodeToString(bytes, Base64.NO_WRAP)
            prefs.edit().putString(DB_PASSPHRASE_KEY, passphrase).apply()
        }
        
        return passphrase.toByteArray()
    }

    fun saveEncryptedString(context: Context, key: String, value: String) {
        getEncryptedPrefs(context).edit().putString(key, value).apply()
    }

    fun getEncryptedString(context: Context, key: String): String? {
        return getEncryptedPrefs(context).getString(key, null)
    }

    fun clearSecurePrefs(context: Context) {
        getEncryptedPrefs(context).edit().clear().apply()
    }
}
