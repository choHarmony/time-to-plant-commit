package com.example.gratify.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.gratify.R
import com.example.gratify.databinding.ActivityMainBinding
import com.example.gratify.model.ContinueCommitDaySharedPreferences
import com.example.gratify.model.EncryptedGithubIdSharedPreferences
import com.example.gratify.model.TimeSharedPreferences
import com.example.gratify.utils.AlarmReceiver
import com.example.gratify.utils.DeleteAlertedStoreReceiver
import com.example.gratify.viewmodel.MainViewModel
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var time = ""
    var notiHour = 0
    var notiMin = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val mainViewModel = MainViewModel(
            TimeSharedPreferences(applicationContext),
            ContinueCommitDaySharedPreferences(applicationContext)
        )
        binding.mainvm = mainViewModel

        notiHour = TimeSharedPreferences(applicationContext).getHour().toInt()
        notiMin = TimeSharedPreferences(applicationContext).getMin().toInt()

        //val mainViewModel = MainViewModel(TimeSharedPreferences(applicationContext), ContinueCommitDaySharedPreferences(applicationContext))

        changeIdTextColor()
        changeFarmIdTextColor()
        setInitialTime()
        changeTimeTextColor()
        setMyFarm()

        //mainViewModel.loadCommitEvent(applicationContext)
        changeDayTextColor(ContinueCommitDaySharedPreferences(applicationContext).getDay())
        //Toast.makeText(this, ContinueCommitDaySharedPreferences(applicationContext).getAlertedToday().toString(), Toast.LENGTH_LONG).show()

        initializeAlertedStore()
        initializeCountStore()
        if (!ContinueCommitDaySharedPreferences(applicationContext).getAlertedToday()) {
            sendNotification()
        }

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
            changeTimeTextColor()
            if (!ContinueCommitDaySharedPreferences(applicationContext).getAlertedToday()) {
                sendNotification()
            }
        })

        mainViewModel.editTimeMin.observe(this, androidx.lifecycle.Observer { newTime ->
            notiMin = newTime.toInt()
            if (newTime.toInt() != 0) {
                time += " ${newTime}분"
            }
            changeTimeTextColor()
            if (!ContinueCommitDaySharedPreferences(applicationContext).getAlertedToday()) {
                sendNotification()
            }
        })

        binding.btnChangeTime.setOnClickListener {
            mainViewModel.showTimePickerDialog(this)
        }




    }


    private fun initializeCountStore() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, DeleteAlertedStoreReceiver::class.java)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.DAY_OF_MONTH, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }


    private fun initializeAlertedStore() {

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, DeleteAlertedStoreReceiver::class.java)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }


    private fun setMyFarm() {
        val countPref = ContinueCommitDaySharedPreferences(applicationContext)
        val alertedDay = countPref.getAlertedDay()

        for (dayIndex in 0 until alertedDay) {

            val farmViewId = resources.getIdentifier("farm_${dayIndex}", "id", packageName)
            val grassViewId = resources.getIdentifier("grass_${dayIndex}", "id", packageName)
            val textViewId = resources.getIdentifier("complete_text", "id", packageName)

            val farmView = findViewById<FrameLayout>(farmViewId)
            val grassView = findViewById<ImageView>(grassViewId)
            val txtView = findViewById<TextView>(textViewId)

            val grassName = resources.getIdentifier("farm_${dayIndex+1}", "drawable", packageName)
            Log.d("namename", grassName.toString())
            Log.d("namename", farmViewId.toString())

            farmView?.setBackgroundResource(R.drawable.bg_btn_round_green3)
            grassView?.visibility = View.VISIBLE
            grassView?.setImageResource(grassName)

            if (dayIndex >= 6) {
                txtView?.visibility = View.VISIBLE
                findViewById<FrameLayout>(R.id.farm_7).setBackgroundColor(Color.parseColor("#F3F3F3"))
                txtView.setTextColor(Color.parseColor("#F8DDA9"))
            }


        }

    }

    private fun sendNotification() {

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, notiHour)
            set(Calendar.MINUTE, notiMin)
            set(Calendar.SECOND, 0)
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

    private fun changeIdTextColor() {

        val userId = EncryptedGithubIdSharedPreferences(this).readUserGithubId()
        val welcomeText = "$userId 님,\n오늘도 커밋하세요 \uD83C\uDF31"
        val goText = "\uD83D\uDD17 ${userId}네 잔디 농장"

        val spannableString = SpannableString(welcomeText)
        val startIndex = welcomeText.indexOf(userId)
        val endIndex = startIndex + userId.length
        val color = ContextCompat.getColor(this, R.color.gr_green3)

        val spannableStr = SpannableString(goText)
        val stIndex = goText.indexOf(userId)
        val endIdx = stIndex + userId.length

        spannableString.setSpan(
            ForegroundColorSpan(color),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableStr.setSpan(
            ForegroundColorSpan(color),
            stIndex,
            endIdx,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.textWelcome.text = spannableString
        binding.textGithub.text = spannableStr
    }


    private fun changeFarmIdTextColor() {
        val userId = EncryptedGithubIdSharedPreferences(this).readUserGithubId()
        val farmText = "\uD83D\uDE9C ${userId}네 이번 주 잔디 현황"

        val spannableString = SpannableString(farmText)
        val startIndex = farmText.indexOf(userId)
        val endIndex = startIndex + userId.length
        val color = ContextCompat.getColor(this, R.color.gr_green3)

        spannableString.setSpan(
            ForegroundColorSpan(color),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //binding.textGrassFarm.text = spannableString
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

    private fun changeDayTextColor(continueDay: String) {

        val day = "${continueDay}일째"
        val dayText = "연속\n$day\n잔디 심는 중"

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




}