package com.example.gratify.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.gratify.model.ContinueCommitDaySharedPreferences

class DeleteAlertedStoreReceiver(): BroadcastReceiver() {

    private lateinit var continuePref: ContinueCommitDaySharedPreferences

    override fun onReceive(p0: Context?, p1: Intent?) {
        // 0시가 지나면 알림을 받았는지 저장하는 boolean을 false로 초기화시킴
        continuePref = ContinueCommitDaySharedPreferences(p0!!)
        continuePref.alertedToday(false)
        Log.d("되냐?", "alertedToday set to false")
    }
}