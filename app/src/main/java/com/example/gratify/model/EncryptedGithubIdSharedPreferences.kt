package com.example.gratify.model

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedGithubIdSharedPreferences(context: Context) {

    private val masterKeyAlias = MasterKey
        .Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val idStorage = EncryptedSharedPreferences.create(
        context,
        "encrypted_gitId_storage",
        masterKeyAlias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveUserGithubId(userId: String) {
        idStorage.edit().putString("userId", userId).apply()
    }

    fun clearUserGithubId() {
        idStorage.edit().putString("userId", null).apply()
    }

    fun readUserGithubId(): String {
        return idStorage.getString("userId", null).toString()
    }

}