package com.example.gratify.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.gratify.R
import com.example.gratify.databinding.ActivityLoginBinding
import com.example.gratify.viewmodel.ViewModel

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this,
        R.layout.activity_login)
        binding.viewmodel = ViewModel()

    }
}