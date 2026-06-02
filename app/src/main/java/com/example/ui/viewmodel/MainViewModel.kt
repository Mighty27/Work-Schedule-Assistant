package com.example.ui.viewmodel

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.local.ScheduleEntity
import com.example.data.local.SettingsPreferences
import com.example.data.model.Content
import com.example.data.model.GenerateContentRequest
import com.example.data.model.InlineData
import com.example.data.model.Part
import com.example.data.remote.RetrofitClient
import com.example.data.repository.ScheduleRepository
import com.example.util.AlarmHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UiState(
    val selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val isScanning: Boolean = false,
    val scanError: String? = null
)

class MainViewModel(
    private val repository: ScheduleRepository,
    private val preferences: SettingsPreferences,
    private val alarmHelper: AlarmHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    val allSchedules = repository.allSchedules.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun getTodaySchedule(): Flow<ScheduleEntity?> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return repository.getScheduleForDate(today)
    }

    fun insertSchedule(schedule: ScheduleEntity) {
        viewModelScope.launch {
            repository.insert(schedule)
            alarmHelper.scheduleDepartureAlarm(schedule.date, schedule.departureTime, preferences.reminderMinutes)
        }
    }

    fun updateSchedule(schedule: ScheduleEntity) {
        viewModelScope.launch {
            repository.update(schedule)
        }
    }

    fun deleteSchedule(id: Int) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }

    fun scanScheduleImage(bitmap: Bitmap) {
        _uiState.update { it.copy(isScanning = true, scanError = null) }
        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val base64Image = bitmap.toBase64()
                val prompt = "Ekstrak jadwal kerja dari gambar. Kembalikan HANYA format JSON murni tanpa markdowns ```json atau apapun, dengan fields: \\\"title\\\" (String, default 'Bekerja'), \\\"date\\\" (String YYYY-MM-DD, default hari ini), \\\"departureTime\\\" (String HH:MM), \\\"clockInTime\\\" (String HH:MM), \\\"clockOutTime\\\" (String HH:MM)."
                
                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(
                                Part(text = prompt),
                                Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Image))
                            )
                        )
                    )
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val text = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                
                if (text != null) {
                    val cleanJson = text.replace("```json", "").replace("```", "").trim()
                    val jsonElement = Json.parseToJsonElement(cleanJson).jsonObject
                    
                    val title = jsonElement["title"]?.jsonPrimitive?.content ?: "Kerja"
                    val date = jsonElement["date"]?.jsonPrimitive?.content ?: _uiState.value.selectedDate
                    val departureTime = jsonElement["departureTime"]?.jsonPrimitive?.content ?: "07:00"
                    val clockInTime = jsonElement["clockInTime"]?.jsonPrimitive?.content ?: "08:00"
                    val clockOutTime = jsonElement["clockOutTime"]?.jsonPrimitive?.content ?: "17:00"

                    val parsedSchedule = ScheduleEntity(
                        title = title,
                        date = date,
                        departureTime = departureTime,
                        clockInTime = clockInTime,
                        clockOutTime = clockOutTime
                    )
                    insertSchedule(parsedSchedule)
                } else {
                    _uiState.update { it.copy(scanError = "Gagal memproses gambar.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(scanError = e.message ?: "Terjadi kesalahan") }
            } finally {
                _uiState.update { it.copy(isScanning = false) }
            }
        }
    }

    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    // Settings
    fun saveOfficeLocation(lat: Float, lng: Float) {
        preferences.officeLat = lat
        preferences.officeLng = lng
        preferences.officeConfigured = true
    }

    fun getOfficeLocation(): Pair<Float, Float>? {
        return if (preferences.officeConfigured) {
            Pair(preferences.officeLat, preferences.officeLng)
        } else null
    }

    fun saveReminderMinutes(mins: Int) {
        preferences.reminderMinutes = mins
    }
}
