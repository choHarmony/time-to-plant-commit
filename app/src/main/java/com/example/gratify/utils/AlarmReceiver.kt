package com.example.gratify.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver: BroadcastReceiver() {

    private lateinit var sendNotification: SendNotification

    override fun onReceive(p0: Context?, p1: Intent?) {
        sendNotification = SendNotification(p0!!)
        sendNotification.deliverNoti()
    }

}