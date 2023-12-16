package com.example.gratify.viewmodel

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gratify.model.*
import com.example.gratify.view.MainActivity
import com.example.gratify.view.SettingActivity
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(val timeSharePref: TimeSharedPreferences, val continueDayPref: ContinueCommitDaySharedPreferences) : ViewModel() {

    private val _editTimeHour = MutableLiveData<String>()
    val editTimeHour: LiveData<String>
        get() = _editTimeHour

    private val _editTimeMin = MutableLiveData<String>()
    val editTimeMin: LiveData<String>
        get() = _editTimeMin

    fun showTimePickerDialog(context: Context) {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                _editTimeHour.value = hourOfDay.toString()
                _editTimeMin.value = minute.toString()

                timeSharePref.setHour(hourOfDay.toString())
                timeSharePref.setMin(minute.toString())
            },
            timeSharePref.getHour().toInt(),
            timeSharePref.getMin().toInt(),
            false
        )

        timePickerDialog.show()
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


    fun goToFarm(view: View) {
        val userId = EncryptedGithubIdSharedPreferences(view.context).readUserGithubId()
        val url = "https://ghchart.rshah.org/${userId}"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        view.context.startActivity(intent)
    }

    fun goToGithub(view: View) {
        val userId = EncryptedGithubIdSharedPreferences(view.context).readUserGithubId()
        val url = "https://github.com/${userId}"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        view.context.startActivity(intent)
    }


    fun openSetting(view: View) {
        val intent = Intent(view.context, SettingActivity::class.java)
        view.context.startActivity(intent)
    }


}