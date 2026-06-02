package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ScheduleEntity
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val schedules by viewModel.allSchedules.collectAsStateWithLifecycle()
    val todaySchedule by viewModel.getTodaySchedule().collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jadwal Kerja") },
                actions = {
                    IconButton(onClick = onNavigateToSettings, modifier = Modifier.testTag("settings_button")) {
                        Icon(Icons.Default.Settings, contentDescription = "Pengaturan")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd, modifier = Modifier.testTag("add_button")) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Jadwal")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Hari Ini",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            if (todaySchedule != null) {
                TodayScheduleCard(schedule = todaySchedule!!, onClockIn = {
                    viewModel.updateSchedule(todaySchedule!!.copy(isClockedIn = true))
                }, onClockOut = {
                    viewModel.updateSchedule(todaySchedule!!.copy(isClockedOut = true))
                })
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(
                        text = "Tidak ada jadwal kerja untuk hari ini.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Text(
                text = "Semua Jadwal",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(schedules) { schedule ->
                    ScheduleItemCard(schedule)
                }
            }
        }
    }
}

@Composable
fun TodayScheduleCard(schedule: ScheduleEntity, onClockIn: () -> Unit, onClockOut: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = schedule.title, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Berangkat: ${schedule.departureTime}")
                Text("Masuk: ${schedule.clockInTime}")
                Text("Pulang: ${schedule.clockOutTime}")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = onClockIn,
                    enabled = !schedule.isClockedIn,
                    modifier = Modifier.weight(1f).testTag("clock_in_button")
                ) {
                    Text(if (schedule.isClockedIn) "Sudah Masuk" else "Absen Masuk")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onClockOut,
                    enabled = schedule.isClockedIn && !schedule.isClockedOut,
                    modifier = Modifier.weight(1f).testTag("clock_out_button")
                ) {
                    Text(if (schedule.isClockedOut) "Sudah Pulang" else "Absen Pulang")
                }
            }
        }
    }
}

@Composable
fun ScheduleItemCard(schedule: ScheduleEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = schedule.title, style = MaterialTheme.typography.titleMedium)
                Text(text = schedule.date, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Masuk: ${schedule.clockInTime} - Pulang: ${schedule.clockOutTime}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
