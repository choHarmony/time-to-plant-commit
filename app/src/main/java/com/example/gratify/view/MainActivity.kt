package com.example.gratify.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.example.gratify.R
import com.example.gratify.databinding.ActivityMainBinding
import com.example.gratify.model.ContinueCommitDaySharedPreferences
import com.example.gratify.model.EncryptedGithubIdSharedPreferences
import com.example.gratify.model.TimeSharedPreferences
import com.example.gratify.utils.AlarmReceiver
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

        mainViewModel.loadCommitEvent(applicationContext)
        changeDayTextColor(ContinueCommitDaySharedPreferences(applicationContext).getDay())

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
            changeTimeTextColor()
            sendNotification()
        })

        mainViewModel.editTimeMin.observe(this, androidx.lifecycle.Observer { newTime ->
            notiMin = newTime.toInt()
            if (newTime.toInt() != 0) {
                time += " ${newTime}분"
            }
            changeTimeTextColor()
            sendNotification()
        })

        binding.btnChangeTime.setOnClickListener {
            mainViewModel.showTimePickerDialog(this)
        }




    }

    private fun setMyFarm() {
        val countPref = ContinueCommitDaySharedPreferences(applicationContext)
        val alertedDay = countPref.getAlertedDay()

        for (dayIndex in 0 until alertedDay) {

            val farmViewId = resources.getIdentifier("farm_$dayIndex", "id", packageName)
            val grassViewId = resources.getIdentifier("grass_${dayIndex}", "id", packageName)

            val farmView = findViewById<FrameLayout>(farmViewId)
            val grassView = findViewById<ImageView>(grassViewId)

            val grassName = resources.getIdentifier("R.drawable.farm_${dayIndex}", "id", packageName)

            farmView?.visibility = View.VISIBLE
            grassView?.setImageResource(grassName)

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

    private fun changeFarmIdTextColor() {
        val userId = EncryptedGithubIdSharedPreferences(this).readUserGithubId()
        val farmText = "\uD83D\uDE9C ${userId}네 잔디 농장"

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

        binding.textGrassFarm.text = spannableString
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