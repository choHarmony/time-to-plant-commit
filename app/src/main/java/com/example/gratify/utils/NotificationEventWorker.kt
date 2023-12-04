package com.example.gratify.utils

import android.content.Context
import android.util.Log
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.gratify.model.EncryptedGithubIdSharedPreferences
import com.example.gratify.model.GithubEventResponse
import com.example.gratify.model.GithubEventService
import com.example.gratify.model.TimeSharedPreferences
import com.example.gratify.view.MainActivity
import com.example.gratify.viewmodel.MainViewModel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationEventWorker(context: Context, workerParams: WorkerParameters, val idSharedPref: EncryptedGithubIdSharedPreferences): Worker(context, workerParams) {

    override fun doWork(): Result {
        loadEvents()
        return Result.success()
    }

    private val clientBuilder = OkHttpClient.Builder()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(clientBuilder.build())
        .build()

    private val retrofitService: GithubEventService = retrofit.create(GithubEventService::class.java)

    fun loadEvents() {
        retrofitService.getUserEvents(idSharedPref.readUserGithubId()).enqueue(object:
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
                    for (index in responseData.indices) {
                        if (responseData[index].type == "pushEvent" && responseData[index].created_at == currentDate) {
                            MainActivity().alert = true
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<GithubEventResponse>>, t: Throwable) {
                Log.d("되냐?", t.message.toString())
            }

        })
    }

    // WorkManager를 설정하고 실행하는 부분
    fun scheduleNotificationEventWorker() {
        val request =
            PeriodicWorkRequestBuilder<NotificationEventWorker>(24, TimeUnit.HOURS).build()

        WorkManager.getInstance().enqueue(request)
    }


}