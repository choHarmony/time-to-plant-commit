package com.example.gratify.model

import android.content.Context

class TimeSharedPreferences(context: Context) {

    private val pref = context.getSharedPreferences("noti_time", Context.MODE_PRIVATE)

    fun getHour(): String {
        return pref.getString("hour", "20").toString()
    }

    fun getMin(): String {
        return pref.getString("min", "0").toString()
    }

    fun setHour(value: String) {
        pref.edit().putString("hour", value).apply()
    }

    fun setMin(value: String) {
        pref.edit().putString("min", value).apply()
    }

}