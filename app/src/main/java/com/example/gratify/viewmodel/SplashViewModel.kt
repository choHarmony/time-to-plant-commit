package com.example.gratify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.security.crypto.EncryptedFile
import com.example.gratify.model.EncryptedGithubIdSharedPreferences

class SplashViewModel(private val githubIdSharedPreferences: EncryptedGithubIdSharedPreferences) : ViewModel() {

    fun isLoggedIn(): Boolean {
        val userId = githubIdSharedPreferences.readUserGithubId()
        return userId != "null"
    }


}