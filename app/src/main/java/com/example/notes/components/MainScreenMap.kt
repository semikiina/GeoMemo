package com.example.notes.components

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.LatLng
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.api.Property
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties


import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await

private const val PERMISSION = "android.permission.ACCESS_FINE_LOCATION"

@SuppressLint("InlinedApi")
@Composable
fun MainScreenMap(){

    val context = LocalContext.current

    val granted = remember {
        mutableStateOf(
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                context,
                PERMISSION
            )
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        granted.value = isGranted
    }

    LaunchedEffect(key1 = Unit) {
        if (!granted.value) {
            launcher.launch(PERMISSION)
        }
    }

    // MAP VIEW

    //val aarhus = LatLng(56.162939, 10.203921)
    val prague = LatLng(50.0755, 14.4378)

    val currentLocation = remember { mutableStateOf(prague) }
    //val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationClient = LocationServices.getFusedLocationProviderClient(context)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            currentLocation.value, 15f
        )
    }

    LaunchedEffect(Unit) {
        if (granted.value) {
            try {
                val location = locationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()
                currentLocation.value = LatLng(location.latitude, location.longitude)

                // Update the camera position
                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                    currentLocation.value,
                    15f
                )
            } catch (e: SecurityException) {
                Log.e("Location", "Permission not granted: ${e.message}")
            } catch (e: Exception) {
                Log.e("Location", "Failed to get location: ${e.message}")
            }
        }
    }

    GoogleMap(
        cameraPositionState=cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true
        )
    ) {}
}