package com.example.gratify.viewmodel

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.gratify.model.EncryptedGithubIdSharedPreferences

class LoginViewModel(private val githubIdSharedPreferences: EncryptedGithubIdSharedPreferences) : ViewModel() {
    val githubId = ObservableField<String>()

    fun loginBtnClicked(view: View) {
        githubIdSharedPreferences.saveUserGithubId(githubId.get().toString())

        if (view.context is Activity) {
            (view.context as Activity).finish()
        }
    }



}
