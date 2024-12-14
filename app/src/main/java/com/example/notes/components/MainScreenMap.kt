package com.example.notes.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.notes.data.getCurrentLocation
import com.example.notes.utils.Screen
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Circle
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

private const val PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
private const val MAX_DISTANCE = 50.0
private const val CIRCLE_POSITION_UPDATE_INTERVAL = 1000 // Interval for location updates in milliseconds

@SuppressLint("InlinedApi")
@Composable
fun MainScreenMap(navController: NavController) {

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    val london = LatLng(51.5074, -0.1278) // Initial location
    val currentLocation = remember { mutableStateOf(london) }

    val granted = remember {
        mutableStateOf(
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, PERMISSION)
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        granted.value = isGranted
    }

    LaunchedEffect(Unit) {
        if (!granted.value) {
            launcher.launch(PERMISSION)
        }
        if (granted.value) {
            currentLocation.value = getCurrentLocation(context)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            currentLocation.value, 15f
        )
    }

    LaunchedEffect(key1 = currentLocation.value) {
        cameraPositionState.move(
            com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                currentLocation.value, 15f
            )
        )
    }

    val circleCenter = remember { mutableStateOf(currentLocation.value) }


    var notes by remember { mutableStateOf<Map<LatLng, Int>>(emptyMap()) }
    var listenerRegistration: ListenerRegistration? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        listenerRegistration = db.collection("notes")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("MainScreenMap", "Error listening to notes: ${exception.localizedMessage}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {

                    val groupedNotes = snapshot.documents.groupBy {
                        val latitude = it.getDouble("latitude")
                        val longitude = it.getDouble("longitude")
                        if (latitude != null && longitude != null) LatLng(latitude, longitude) else null
                    }.filterKeys { it != null }
                        .mapKeys { it.key!! }


                    notes = groupedNotes.mapValues { it.value.size }
                    //Log.i("MainScreenMap", "Updated notes: ${notes.size} locations")
                }
            }
    }

    DisposableEffect(Unit) {
        onDispose {
            listenerRegistration?.remove()
        }
    }


    LaunchedEffect(Unit) {
        if (granted.value) {
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            val locationRequest = LocationRequest.Builder(
                LocationRequest.PRIORITY_HIGH_ACCURACY, CIRCLE_POSITION_UPDATE_INTERVAL.toLong()
            ).build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        val lat = location.latitude
                        val lng = location.longitude


                        circleCenter.value = LatLng(lat, lng)
                        //Log.i("Location", "Location changed: Latitude=$lat, Longitude=$lng")
                    }
                }
            }

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    GoogleMap(
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = granted.value
        ),
        onPOIClick = { poi ->
            val poiLatLng = poi.latLng
            val currentLatLng = currentLocation.value
            val results = FloatArray(1)
            android.location.Location.distanceBetween(
                currentLatLng.latitude,
                currentLatLng.longitude,
                poiLatLng.latitude,
                poiLatLng.longitude,
                results
            )

            val distance = results[0] // Distance in meters
            if (distance <= MAX_DISTANCE) {
                navController.navigate(route = Screen.NotesAtPlace.createRoute(poi.placeId))
            }
        }
    ) {

        Circle(
            center = circleCenter.value,
            radius = MAX_DISTANCE
        )

        notes.forEach { (location, noteCount) ->
            Marker(
                state = MarkerState(position = location),
                title = "Notes: $noteCount",
                icon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                    com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
                )
            )
        }
    }
}
