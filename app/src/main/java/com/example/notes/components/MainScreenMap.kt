package com.example.notes.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState

import com.example.notes.data.getCurrentLocation
import com.example.notes.utils.Screen
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.Circle

private const val PERMISSION = "android.permission.ACCESS_FINE_LOCATION"

@SuppressLint("MissingPermission", "InlinedApi")
@Composable
fun MainScreenMap(navController: NavController) {

    val context = LocalContext.current

    // Initial position set to London
    val london = LatLng(51.5074, -0.1278)
    val currentLocation = remember { mutableStateOf(london) }

    val granted = remember {
        mutableStateOf(
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, PERMISSION))
        }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.i("Location", "Permission was granted")
        } else {
            Log.i("Location", "Permission was NOT granted")
        }
        granted.value = isGranted
    }

    val noteLocations = remember { mutableStateMapOf<String, LatLng>() }

    // Fetch current location and notes from Firebase
    LaunchedEffect(Unit) {
        if (!granted.value) {
            Log.i("Location", "Permission isnt granted")
            launcher.launch(PERMISSION)
        }
        if (granted.value) {
            try {
            // Get user's current location
            currentLocation.value = getCurrentLocation(context)
        }

            // Fetch notes from Firebase Firestore
            val db = FirebaseFirestore.getInstance()
            val notes = db.collection("notes")
                .get()
                .await() // Coroutine-based Firestore request
                .documents

            // Map notes to their locations
            notes.forEach { document ->
                val placeId = document.getString("placeId") ?: return@forEach
                val latitude = document.getDouble("latitude") ?: return@forEach
                val longitude = document.getDouble("longitude") ?: return@forEach

                // Add note location to the map
                noteLocations[placeId] = LatLng(latitude, longitude)
            }
        } catch (e: Exception) {
            Log.e("MainScreenMap", "Error fetching notes or location: ${e.localizedMessage}")
        }
    }

    // Camera position state to handle map view

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            currentLocation.value, 15f
        )
    }

    // Update camera position when location changes
    LaunchedEffect(currentLocation.value) {
        cameraPositionState.move(
            CameraUpdateFactory.newLatLngZoom(
                currentLocation.value, 15f
            )
        )
    }

    // Display Google Map
    GoogleMap(
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = granted.value
        ),
        onPOIClick = { poi ->
            // Handle POI click (optional navigation)
            Log.i(
                "Location",
                "POI clicked: ${poi.name} at ${poi.latLng.latitude}, ${poi.latLng.longitude} id ${poi.placeId}"
            )
            navController.navigate(route = "notesAtPlace/${poi.placeId}")
        }
    ) {
        Circle(
            center = currentLocation.value,
            radius = 50.0
        )
    }
}