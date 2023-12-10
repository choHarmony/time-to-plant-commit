package com.example.gratify.viewmodel

import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gratify.model.*
import com.example.gratify.view.MainActivity
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


    fun loadCommitEvent(context: Context) {
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
                    val currentDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(
                        Date()
                    )
                    val simpleDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val simpleDateOnly = simpleDate.format(Date())

                    for (index in responseData.indices) {

                        val utcEventDate = responseData[index].created_at
                        val eventDateKorean = convertUtcToKoreanTime(utcEventDate)

                        if (responseData[index].type == "PushEvent" && eventDateKorean == simpleDateOnly) {
                            val continueDay = continueDayPref.getDay().toInt() + 1
                            continueDayPref.setDay(continueDay.toString())
                            break
                        }
//                        else if (eventDateKorean != simpleDateOnly) {
//                            continueDayPref.setDay("0")
//                        }


                    }

                }

            }

            override fun onFailure(call: Call<List<GithubEventResponse>>, t: Throwable) {
                Log.d("되냐?_2", t.message.toString())
            }

        })
    }


}