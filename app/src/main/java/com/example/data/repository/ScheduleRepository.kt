package com.example.data.repository

import com.example.data.local.ScheduleDao
import com.example.data.local.ScheduleEntity
import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val scheduleDao: ScheduleDao) {
    val allSchedules: Flow<List<ScheduleEntity>> = scheduleDao.getAllSchedules()

    fun getScheduleForDate(date: String): Flow<ScheduleEntity?> {
        return scheduleDao.getScheduleForDate(date)
    }

    suspend fun getScheduleForDateSync(date: String): ScheduleEntity? {
        return scheduleDao.getScheduleForDateSync(date)
    }

    suspend fun insert(schedule: ScheduleEntity) {
        scheduleDao.insertSchedule(schedule)
    }

    suspend fun update(schedule: ScheduleEntity) {
        scheduleDao.updateSchedule(schedule)
    }

    suspend fun delete(id: Int) {
        scheduleDao.deleteSchedule(id)
    }
}
