package com.example.gratify.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.gratify.BuildConfig
import com.example.gratify.R
import com.example.gratify.databinding.ActivityMainBinding
import com.example.gratify.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this,
        R.layout.activity_main)
        binding.mainvm = MainViewModel()

    }
}