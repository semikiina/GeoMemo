package com.example.notes.components

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.InspectableModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng

@Composable
fun MainScreenMap(modifier: Modifier = Modifier){

    val context = LocalContext.current

    val mapView = MapView(context).apply {
        onCreate(Bundle())
        getMapAsync(OnMapReadyCallback { googleMap ->
            val aarhus = LatLng(56.162939, 10.203921)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(aarhus, 10f))
        })
    }

    AndroidView(
        factory = {mapView},
        modifier = modifier
    ) { view ->
        view.onResume()
    }
}