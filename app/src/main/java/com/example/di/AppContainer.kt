package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.SettingsPreferences
import com.example.data.repository.ScheduleRepository

class AppContainer(private val context: Context) {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "jadwal_kerja_db"
        ).build()
    }

    val scheduleRepository: ScheduleRepository by lazy {
        ScheduleRepository(database.scheduleDao())
    }

    val settingsPreferences: SettingsPreferences by lazy {
        SettingsPreferences(context)
    }
}
