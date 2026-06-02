package com.example.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.receivers.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AlarmHelper(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleDepartureAlarm(date: String, departureTime: String, reminderMinutes: Int) {
        try {
            // date format: YYYY-MM-DD
            // departureTime format: HH:mm
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val dateObj = format.parse("$date $departureTime") ?: return

            val calendar = Calendar.getInstance()
            calendar.time = dateObj
            calendar.add(Calendar.MINUTE, -reminderMinutes)

            if (calendar.timeInMillis > System.currentTimeMillis()) {
                val intent = Intent(context, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    context, 
                    1001, 
                    intent, 
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
