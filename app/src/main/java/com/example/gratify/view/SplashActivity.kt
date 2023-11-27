package com.example.gratify.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.gratify.R
import com.example.gratify.databinding.ActivitySplashBinding
import com.example.gratify.model.EncryptedGithubIdSharedPreferences
import com.example.gratify.viewmodel.SplashViewModel

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivitySplashBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_splash)
        binding.splashvm = SplashViewModel(EncryptedGithubIdSharedPreferences(applicationContext))

        loadSplash()
    }

    private fun observeLoginState() {
        val splashVm = SplashViewModel(EncryptedGithubIdSharedPreferences(applicationContext))
        if (splashVm.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun loadSplash() {
        observeLoginState()
        finish()
    }



}