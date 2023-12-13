package com.example.gratify.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.gratify.model.ContinueCommitDaySharedPreferences

class DeleteAlertedStoreReceiver(val continuePref: ContinueCommitDaySharedPreferences): BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        // 0시가 지나면 알림을 받았는지 저장하는 boolean을 false로 초기화시킴
        continuePref.alertedToday(false)
    }
}