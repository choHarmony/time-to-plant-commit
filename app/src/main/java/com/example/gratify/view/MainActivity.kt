package com.example.gratify.view

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.os.SystemClock
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.gratify.R
import com.example.gratify.databinding.ActivityMainBinding
import com.example.gratify.model.EncryptedGithubIdSharedPreferences
import com.example.gratify.model.GithubEventResponse
import com.example.gratify.model.GithubEventService
import com.example.gratify.model.TimeSharedPreferences
import com.example.gratify.utils.AlarmReceiver
import com.example.gratify.utils.NotificationEventWorker
import com.example.gratify.utils.SendNotification
import com.example.gratify.viewmodel.MainViewModel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var time = ""
    var notiHour = 0
    var notiMin = 0
    var alert = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainvm = MainViewModel(TimeSharedPreferences(applicationContext), EncryptedGithubIdSharedPreferences(applicationContext))

        notiHour = TimeSharedPreferences(applicationContext).getHour().toInt()
        notiMin = TimeSharedPreferences(applicationContext).getMin().toInt()

        val mainViewModel = MainViewModel(TimeSharedPreferences(applicationContext), EncryptedGithubIdSharedPreferences(applicationContext))
        binding.lifecycleOwner = this

        changeIdTextColor()
        setInitialTime()
        changeTimeTextColor()
        changeDayTextColor()

        sendNotification()



        mainViewModel.editTimeHour.observe(this, androidx.lifecycle.Observer { newTime ->
            time = ""
            notiHour = newTime.toInt()
            if (newTime.toInt() > 12) {
                time = "오후 ${newTime.toInt()-12}시"
            } else if (newTime.toInt() == 12) {
                time = "오후 12시"
            } else {
                time = "오전 ${newTime}시"
            }
        })

        mainViewModel.editTimeMin.observe(this, androidx.lifecycle.Observer { newTime ->
            notiMin = newTime.toInt()
            if (newTime.toInt() != 0) {
                time += " ${newTime}분"
            }
            changeTimeTextColor()
        })

        binding.btnChangeTime.setOnClickListener {
            mainViewModel.showTimePickerDialog(this)
        }

    }

    private fun changeIdTextColor() {

        val userId = EncryptedGithubIdSharedPreferences(this).readUserGithubId()
        val welcomeText = "$userId 님,\n오늘도 커밋하세요 \uD83C\uDF31"

        val spannableString = SpannableString(welcomeText)
        val startIndex = welcomeText.indexOf(userId)
        val endIndex = startIndex + userId.length
        val color = ContextCompat.getColor(this, R.color.gr_green3)

        spannableString.setSpan(
            ForegroundColorSpan(color),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.textWelcome.text = spannableString
    }

    private fun changeTimeTextColor() {

        val timeText = "매일\n${time}에\n알림을 보내드려요"

        val spannableString = SpannableString(timeText)
        val startIndex = timeText.indexOf(time)
        val endIndex = startIndex + time.length
        val color = ContextCompat.getColor(this, R.color.gr_yellow)

        spannableString.setSpan(
            ForegroundColorSpan(color),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.cardAlarmText.text = spannableString
    }

    private fun setInitialTime() {
        var hour = TimeSharedPreferences(applicationContext).getHour().toInt()
        var min = TimeSharedPreferences(applicationContext).getMin().toInt()

        if (hour > 12) {
            hour -= 12
            time += "오후 ${hour}시"
        } else if (hour == 12) {
            time += "오후 12시"
        } else {
            time += "오전 ${hour}시"
        }

        if (min != 0) {
            time += " ${min}분"
        }
    }

    private fun changeDayTextColor() {

        val day = "3일째"
        val dayText = "연속\n3일째\n잔디 심는 중"

        val spannableString = SpannableString(dayText)
        val startIndex = dayText.indexOf(day)
        val endIndex = startIndex + day.length
        val color = ContextCompat.getColor(this, R.color.gr_yellow)

        spannableString.setSpan(
            ForegroundColorSpan(color),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.cardContinueText.text = spannableString
    }




    private fun sendNotification() {

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, notiHour)
            set(Calendar.MINUTE, notiMin)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

    }

}