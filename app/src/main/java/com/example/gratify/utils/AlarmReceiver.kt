package com.example.gratify.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.gratify.view.MainActivity

class AlarmReceiver : BroadcastReceiver() {

    private lateinit var sendNotification: SendNotification

    override fun onReceive(p0: Context?, p1: Intent?) {
        sendNotification = SendNotification(p0!!)

        // 비동기적으로 determineAlert를 호출
        sendNotification.determineAlert(object : SendNotification.AlertCallback {
            override fun onAlertDetermined(alert: Boolean) {
                // determineAlert에서 alert 값을 받아와서 MainActivity.alert 값을 변경
                MainActivity.alert = alert

                // MainActivity.alert 값을 변경한 후에 deliverNoti를 호출
                sendNotification.deliverNoti()
            }
        })
    }
}
