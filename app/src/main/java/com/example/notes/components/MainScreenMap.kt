package com.example.notes.components


import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await
import java.util.Arrays


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

    val prague = LatLng(50.0755, 14.4378)

    val currentLocation = remember { mutableStateOf(prague) }
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

    // TODO
    // 1) Get nearest location from current location
    // 2)

    GoogleMap(
        cameraPositionState=cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true
        )
    ) {}

    val placeFields = Arrays.asList(Place.Field.ID, Place.Field.DISPLAY_NAME)
    val center = currentLocation.value
    val circle = CircularBounds.newInstance(center,  /* radius = */100.0)

    val searchNearbyRequest =
        SearchNearbyRequest.builder(circle, placeFields)
            .setMaxResultCount(10)
            .build()

    Places.initialize(context, "AIzaSyDf6eNeqpJGs3GGeBELEKmTF1alM0OaUnQ")
    val placesClient: PlacesClient = Places.createClient(context)

    placesClient.searchNearby(searchNearbyRequest)
        .addOnSuccessListener { response ->
            val places: List<Place> = response.getPlaces()
            places.forEach{ place ->
                place.name?.let { Log.i( "PLACE", it.toString()) }
            }
        }
}