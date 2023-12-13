package com.example.gratify.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.gratify.model.ContinueCommitDaySharedPreferences

class DeleteCountReceiver(): BroadcastReceiver() {

    private lateinit var continuePref: ContinueCommitDaySharedPreferences

    override fun onReceive(p0: Context?, p1: Intent?) {
        continuePref = ContinueCommitDaySharedPreferences(p0!!)
        continuePref.storeAlertedDay(0)
    }
}