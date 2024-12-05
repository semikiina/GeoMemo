package com.example.notes.components


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
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

@SuppressLint("InlinedApi")
@Composable
fun MainScreenMap(navController: NavController){

    val context = LocalContext.current

    val london = LatLng(51.5074, -0.1278)   // initial location

    val currentLocation = remember { mutableStateOf(london) }
    LaunchedEffect(Unit) {
        currentLocation.value = getCurrentLocation(context)
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            currentLocation.value, 15f
        )
    }

    var circle = Circle(
        center = currentLocation.value,
        radius = 50.0)

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
        onPOIClick = { poi ->
            // Handle POI click
            Log.i("Location","POI clicked: ${poi.name} at ${poi.latLng.latitude}, ${poi.latLng.longitude} id ${poi.placeId}")
            navController.navigate(route = Screen.NotesAtPlace.createRoute(poi.placeId))
        }
    ) {
        Circle(
            center = currentLocation.value,
            radius = 50.0
        )
    }
}