package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val date: String, // YYYY-MM-DD format
    val departureTime: String, // HH:MM
    val clockInTime: String, // HH:MM
    val clockOutTime: String, // HH:MM
    val isClockedIn: Boolean = false,
    val isClockedOut: Boolean = false
)
