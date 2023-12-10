package com.example.gratify.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.gratify.model.ContinueCommitDaySharedPreferences
import com.example.gratify.view.MainActivity

class AlarmReceiver: BroadcastReceiver() {

    private lateinit var sendNotification: SendNotification

    override fun onReceive(p0: Context?, p1: Intent?) {
        sendNotification = SendNotification(p0!!, ContinueCommitDaySharedPreferences(p0))
        sendNotification.deliverNoti()
    }

}
