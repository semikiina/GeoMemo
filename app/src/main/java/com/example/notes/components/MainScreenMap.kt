package com.example.notes.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.InspectableModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import kotlin.contracts.contract
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.tasks.await

private const val PERMISSION = "android.permission.ACCESS_FINE_LOCATION"

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainScreenMap(modifier: Modifier = Modifier){

    val context = LocalContext.current

    val location = remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val client = remember { LocationServices.getFusedLocationProviderClient(context) }

    /*
    LaunchedEffect(key1 = Unit) {
        try {
            val res =
                client.getCurrentLocation(android.location.LocationRequest.QUALITY_HIGH_ACCURACY, null)
                    .await()
            location.value = LatLng(res.latitude, res.longitude)
        } catch (ex: SecurityException) {
            Log.v(this::class.qualifiedName, ex.message.toString())
        }
    }
*/
    val granted = remember {
        mutableStateOf(
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                context,
                PERMISSION
            )
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = RequestPermission()
    ) { isGranted: Boolean ->
        granted.value = isGranted
    }

    LaunchedEffect(key1 = Unit) {
        if (!granted.value) {
            launcher.launch(PERMISSION)
        }
    }

    val mapView = MapView(context).apply {
        //onCreate(Bundle())
        getMapAsync(OnMapReadyCallback { googleMap ->
            val aarhus = LatLng(56.162939, 10.203921)
            val aarhus_2 = LatLng(9.162939, 9.203921)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(aarhus_2, 10f))
            //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(9.162939, 9.203921), 10f))
            //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(aarhus, 10f))
        })
    }

    DisposableEffect(mapView) {
        mapView.onStart()
        onDispose {
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    AndroidView(
        factory = {mapView},
        modifier = modifier
    ) { view ->
        view.onResume()
    }
}