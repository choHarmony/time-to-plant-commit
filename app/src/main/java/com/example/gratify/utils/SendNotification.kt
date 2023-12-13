package com.example.gratify.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.gratify.R
import com.example.gratify.model.*
import com.example.gratify.view.MainActivity
import com.example.gratify.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class SendNotification(private val context: Context, val continueDayPref: ContinueCommitDaySharedPreferences) {

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

    fun convertUtcToKoreanTime(utcTime: String): String? {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")

        val koreanFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        koreanFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")

        return try {
            val date = utcFormat.parse(utcTime)
            koreanFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            e.printStackTrace().toString()
        }
    }

    fun deliverNoti() {
        determineNotiAlert { isAlert ->

            Log.d("되냐?", "alert in deliverNoti: $isAlert")

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

            // 여기만 if문 처리해줘야 함. 위에까지 다 if문으로 처리하면 커밋을 안 했어도 알림이 안 울려버림
            Log.d("되냐?", "alert in deliverNoti before alert code: $isAlert")
            if (isAlert) {
                notiManager.notify(NOTIFICATION_ID, builder.build())
                continueDayPref.alertedToday(true)
                continueDayPref.setDay("0")
            }
        }


    }


    fun determineNotiAlert(callback: (Boolean) -> Unit) {
        val clientBuilder = OkHttpClient.Builder()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()

        val retrofitService: GithubEventService = retrofit.create(GithubEventService::class.java)

        retrofitService.getUserEvents(EncryptedGithubIdSharedPreferences(context).readUserGithubId()).enqueue(object:
            Callback<List<GithubEventResponse>> {
            override fun onResponse(
                call: Call<List<GithubEventResponse>>,
                response: Response<List<GithubEventResponse>>
            ) {
                val responseData = response.body()
                if (responseData != null) {
                    Log.d("되냐?", "${responseData[0].type}, ${convertUtcToKoreanTime(responseData[0].created_at)}")
                    val currentDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(
                        Date()
                    )
                    val simpleDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val simpleDateOnly = simpleDate.format(Date())

                    var isAlert = true

                    for (index in responseData.indices) {
                        val utcEventDate = responseData[index].created_at
                        val eventDateKorean = convertUtcToKoreanTime(utcEventDate)
                        Log.d("되냐?", "${index}, ${responseData[0].type}, ${eventDateKorean}, $simpleDateOnly")

                        if (responseData[index].type == "PushEvent" && eventDateKorean == simpleDateOnly) {
                            Log.d("되냐?", "${index}, ${responseData[0].type}, ${eventDateKorean}, $simpleDateOnly")
                            isAlert = false
                            val continueDay = continueDayPref.getDay().toInt() + 1
                            continueDayPref.setDay(continueDay.toString())
                            val count = continueDayPref.getAlertedDay()
                            continueDayPref.storeAlertedDay(count+1)
                            Log.d("되냐?", "alert: $isAlert")
                            break

                        }
                    }

                    callback(isAlert)

                }

            }

            override fun onFailure(call: Call<List<GithubEventResponse>>, t: Throwable) {
                Log.d("되냐?", t.message.toString())
            }

        })


    }

    companion object {
        const val CHANNEL_ID = "channel_id"
        const val CHANNEL_NAME = "channel_name"
        const val NOTIFICATION_ID = 0
    }

}