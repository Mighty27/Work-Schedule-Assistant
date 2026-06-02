package com.example.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.MainViewModel
import com.example.util.LocationHelper
import com.example.util.AlarmHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var lat by remember { mutableStateOf(viewModel.getOfficeLocation()?.first?.toString() ?: "") }
    var lng by remember { mutableStateOf(viewModel.getOfficeLocation()?.second?.toString() ?: "") }
    
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Lokasi Kantor", style = MaterialTheme.typography.titleMedium)

            if (!locationPermissionsState.allPermissionsGranted) {
                Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }) {
                    Text("Beri Akses Lokasi")
                }
            }

            OutlinedTextField(
                value = lat,
                onValueChange = { lat = it },
                label = { Text("Latitude") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lng,
                onValueChange = { lng = it },
                label = { Text("Longitude") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    try {
                        val latitude = lat.toFloat()
                        val longitude = lng.toFloat()
                        viewModel.saveOfficeLocation(latitude, longitude)
                        
                        if (locationPermissionsState.allPermissionsGranted) {
                            val helper = LocationHelper(context)
                            helper.setupOfficeGeofence(latitude.toDouble(), longitude.toDouble())
                            Toast.makeText(context, "Geofence Kantor Diaktifkan", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Input lokasi tidak valid", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Lokasi")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Tip: Masukkan koordinat kantor Anda untuk menerima notifikasi saat tiba di kantor.", style = MaterialTheme.typography.bodySmall)

        }
    }
}
