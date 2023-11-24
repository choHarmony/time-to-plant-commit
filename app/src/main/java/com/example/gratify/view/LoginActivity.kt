package com.example.gratify.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.gratify.R
import com.example.gratify.databinding.ActivityLoginBinding
import com.example.gratify.model.EncryptedGithubIdSharedPreferences
import com.example.gratify.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    companion object {
        lateinit var githubIdSharedPreferences: EncryptedGithubIdSharedPreferences
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        githubIdSharedPreferences = EncryptedGithubIdSharedPreferences(applicationContext)

        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_login)
        binding.loginvm = LoginViewModel(githubIdSharedPreferences)



    }




}