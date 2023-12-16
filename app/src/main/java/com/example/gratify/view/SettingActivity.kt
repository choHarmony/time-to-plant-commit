package com.example.gratify.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.gratify.R
import com.example.gratify.databinding.ActivitySettingBinding
import com.example.gratify.model.EncryptedGithubIdSharedPreferences
import com.example.gratify.viewmodel.SettingViewModel

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        val settingViewModel = SettingViewModel(EncryptedGithubIdSharedPreferences(applicationContext))
        binding.settingvm = settingViewModel

        binding.btnLogout.setOnClickListener {
            settingViewModel.logout(this)
        }
    }
}