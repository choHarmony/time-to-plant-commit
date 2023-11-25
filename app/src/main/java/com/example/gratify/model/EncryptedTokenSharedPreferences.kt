package com.example.gratify.model

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedTokenSharedPreferences(context: Context) {

    private val masterKeyAlias = MasterKey
        .Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val tokenStorage = EncryptedSharedPreferences.create(
        context,
        "encrypted_gitToken_storage",
        masterKeyAlias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    

    fun loadToken(): String? {
        return tokenStorage.getString("gitToken", null)
    }





}