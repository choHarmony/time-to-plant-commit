package com.example.gratify.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.gratify.model.EncryptedGithubIdSharedPreferences
import com.example.gratify.view.LoginActivity

class SettingViewModel(val idSharedPref: EncryptedGithubIdSharedPreferences) : ViewModel() {

    fun logout(context: Context) {
        idSharedPref.clearUserGithubId()

        val intent = Intent(context, LoginActivity::class.java)
        // 로그인 액티비티 제외 모두 종료 (main, setting)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)

    }

}