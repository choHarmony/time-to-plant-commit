package com.example.gratify.viewmodel

import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gratify.model.EncryptedGithubIdSharedPreferences
import com.example.gratify.model.GithubEventResponse
import com.example.gratify.model.GithubEventService
import com.example.gratify.model.TimeSharedPreferences
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainViewModel(val timeSharePref: TimeSharedPreferences) : ViewModel() {


    private val clientBuilder = OkHttpClient.Builder()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(clientBuilder.build())
        .build()

    private val retrofitService: GithubEventService = retrofit.create(GithubEventService::class.java)

    fun loadEvents() {
        retrofitService.getUserEvents("choHarmony").enqueue(object: Callback<List<GithubEventResponse>> {
            override fun onResponse(
                call: Call<List<GithubEventResponse>>,
                response: Response<List<GithubEventResponse>>
            ) {
                val responseData = response.body()
                if (responseData != null) {
                    Log.d("되냐?", responseData.toString())
                }
            }

            override fun onFailure(call: Call<List<GithubEventResponse>>, t: Throwable) {
                Log.d("되냐?", t.message.toString())
            }

        })
    }


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


}