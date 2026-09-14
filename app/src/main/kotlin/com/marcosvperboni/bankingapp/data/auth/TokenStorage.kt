package com.marcosvperboni.bankingapp.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Persists the JWT and current user id outside of process memory, encrypted at rest. */
@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        "secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun saveSession(token: String, userId: String) {
        prefs.edit().putString(KEY_TOKEN, token).putString(KEY_USER_ID, userId).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val KEY_TOKEN = "auth_token"
        const val KEY_USER_ID = "user_id"
    }
}
