package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.SettingsPreferences
import com.example.data.repository.ScheduleRepository
import com.example.util.AlarmHelper

class MainViewModelFactory(
    private val repository: ScheduleRepository,
    private val preferences: SettingsPreferences,
    private val alarmHelper: AlarmHelper
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository, preferences, alarmHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
