package com.example.gratify.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.gratify.R
import com.example.gratify.model.EncryptedGithubIdSharedPreferences
import com.example.gratify.model.TimeSharedPreferences
import com.example.gratify.view.MainActivity
import com.example.gratify.viewmodel.MainViewModel

class SendNotification(private val context: Context) {

    private var notiManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotiChannel()
    }

    private fun createNotiChannel() {
        val notificationChannel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                .apply {
                    enableVibration(false)
                    description = "description"
                }
        notiManager.createNotificationChannel(notificationChannel)
    }

    fun deliverNoti() {
        val mainViewModel = MainViewModel(TimeSharedPreferences(context), EncryptedGithubIdSharedPreferences(context))
        mainViewModel.loadEvents()

        if (MainActivity().alert) {
            val intent = Intent(context, SendNotification::class.java)

            val pendingIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.gratify_grass_round_logo)
                .setContentTitle("오늘은 커밋을 하지 않으셨네요!")
                .setContentText("얼른 잔디 심으러 가요\uD83C\uDF31")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)


            notiManager.notify(NOTIFICATION_ID, builder.build())
        }
        }

    companion object {
        const val CHANNEL_ID = "channel_id"
        const val CHANNEL_NAME = "channel_name"
        const val NOTIFICATION_ID = 0
    }
    
}