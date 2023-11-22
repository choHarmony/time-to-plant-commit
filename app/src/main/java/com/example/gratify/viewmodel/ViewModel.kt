package com.example.gratify.viewmodel

import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.gratify.view.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class ViewModel(application: Application) : AndroidViewModel(application) {
    val githubId = ObservableField<String>()
    val context = getApplication<Application>().applicationContext

    val masterKeyAlias = MasterKey
        .Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val idStorage = EncryptedSharedPreferences.create(
        context,
        "encrypted_gitId_storage",
        masterKeyAlias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )



    fun storeEncodedId(view: View) {
        idStorage.edit().putString("userId", githubId.toString()).apply()

    }
}
