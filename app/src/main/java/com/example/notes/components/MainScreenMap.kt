package com.example.notes.components


import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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

import com.example.notes.data.getCurrentLocation
import com.example.notes.ui.theme.NotesTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState


@SuppressLint("InlinedApi")
@Composable
fun MainScreenMap(){

    val context = LocalContext.current

    // MAP VIEW
    //val prague = LatLng(50.0755, 14.4378)
    val london = LatLng(51.5074, -0.1278)


    val currentLocation = remember { mutableStateOf(london) }
    LaunchedEffect(Unit) {
        currentLocation.value = getCurrentLocation(context)
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            currentLocation.value, 15f
        )
    }

    LaunchedEffect(key1 = currentLocation.value) {
        currentLocation.value.let {
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(
                    it, 15f
                )
            )
        }
    }

    GoogleMap(
        cameraPositionState=cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true
        ),

    ) {
        Marker(
            state = MarkerState(
                position = currentLocation.value
            )

        )
    }
}

@Preview
@Composable
fun MainMapPreview(){
    NotesTheme {
        MainScreenMap()

    }
}