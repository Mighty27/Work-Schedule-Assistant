package com.example.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.util.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val helper = NotificationHelper(context)
        helper.showDepartureReminder(
            "Waktunya Berangkat!",
            "Jangan lupa absen masuk saat tiba di kantor."
        )
    }
}
