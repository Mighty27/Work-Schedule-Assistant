package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

class SettingsPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    var officeLat: Float
        get() = prefs.getFloat("office_lat", 0f)
        set(value) = prefs.edit().putFloat("office_lat", value).apply()

    var officeLng: Float
        get() = prefs.getFloat("office_lng", 0f)
        set(value) = prefs.edit().putFloat("office_lng", value).apply()

    var reminderMinutes: Int
        get() = prefs.getInt("reminder_minutes", 30) // 30 mins before departure
        set(value) = prefs.edit().putInt("reminder_minutes", value).apply()

    var officeConfigured: Boolean
        get() = prefs.getBoolean("office_configured", false)
        set(value) = prefs.edit().putBoolean("office_configured", value).apply()
}
