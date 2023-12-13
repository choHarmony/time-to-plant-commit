package com.example.gratify.model

import android.content.Context

class ContinueCommitDaySharedPreferences(context: Context) {

    private val pref = context.getSharedPreferences("commit_continue_day", Context.MODE_PRIVATE)

    fun getDay(): String {
        return pref.getString("continue_day", "0").toString()
    }

    fun setDay(value: String) {
        pref.edit().putString("continue_day", value).apply()
    }

    fun alertedToday(value: Boolean) {
        pref.edit().putString("changed_today", value.toString()).apply()
    }

    fun getAlertedToday(): Boolean {
        return pref.getString("changed_today", "").toBoolean()
    }

    fun storeAlertedDay(day: Int) {
        pref.edit().putInt("day_count", day).apply()
    }

    fun getAlertedDay(): Int {
        return pref.getInt("day_count", 0)
    }

}