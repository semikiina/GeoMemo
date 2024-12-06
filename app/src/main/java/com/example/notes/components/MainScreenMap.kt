package com.example.notes.components


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState

import com.example.notes.data.getCurrentLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission", "InlinedApi")
@Composable
fun MainScreenMap(navController: NavController) {

    val context = LocalContext.current

    // Initial position set to London
    val london = LatLng(51.5074, -0.1278)
    val currentLocation = remember { mutableStateOf(london) }
    val noteLocations = remember { mutableStateMapOf<String, LatLng>() }

    // Fetch current location and notes from Firebase
    LaunchedEffect(Unit) {
        try {
            // Get user's current location
            currentLocation.value = getCurrentLocation(context)

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
            isMyLocationEnabled = true // Enable user location
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
        // Add markers for each note location
        noteLocations.forEach { (placeId, location) ->
            Marker(
                state = MarkerState(position = location),
                title = "Note at ${location.latitude}, ${location.longitude}",
                snippet = "Click for details",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                onClick = {
                    // Navigate to the notes screen for this location
                    navController.navigate(route = "notesAtPlace/$placeId")
                    true
                }
            )
        }
    }
}

@Preview
@Composable
fun MainMapPreview() {
    // Placeholder for preview
}