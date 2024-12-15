package com.example.notes.data

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import kotlinx.coroutines.tasks.await

private const val PERMISSION = "android.permission.ACCESS_FINE_LOCATION"


suspend fun getCurrentLocation(context: Context): LatLng {

    val granted =
        mutableStateOf(
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                context,
                PERMISSION
            )
        )

    val prague = LatLng(50.0755, 14.4378)
    val currentLocation = mutableStateOf(prague)

    if (granted.value){     // if location permission is granted
        val locationClient = LocationServices.getFusedLocationProviderClient(context)

        try{
            val location = locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()
            currentLocation.value = LatLng(location.latitude, location.longitude)
        } catch (e: SecurityException) {
            Log.e("Location", "Permission not granted: ${e.message}")
        } catch (e: Exception) {
            Log.e("Location", "Failed to get location: ${e.message}")
        }
    } else {
        Log.e("Location", "Location NOT granted")
    }

    return currentLocation.value
}

suspend fun getNearestPlaces(context: Context): List<Place> {
    Places.initialize(context, "AIzaSyDf6eNeqpJGs3GGeBELEKmTF1alM0OaUnQ")   // TEMPORARY!!! will not be hardcoded

    val placesClient: PlacesClient = Places.createClient(context)
    val center = getCurrentLocation(context)
    val circle = CircularBounds.newInstance(center, 100.0)
    val placesField = listOf(Place.Field.ID, Place.Field.DISPLAY_NAME, Place.Field.LAT_LNG, Place.Field.FORMATTED_ADDRESS)

    val searchNearbyRequest =
        SearchNearbyRequest.builder(circle, placesField)
            .setMaxResultCount(1)                       // returns just one nearest location
            .build()

    return try {
        kotlinx.coroutines.suspendCancellableCoroutine { continuation ->

        placesClient.searchNearby(searchNearbyRequest)
            .addOnSuccessListener { response ->
                val places = response.places
                /*
                Log.i("Location", "UNSORTED")
                places.forEach{ place ->
                    place.displayName.let { Log.i( "Location", it.toString()) }
                }
                val sortedPlaces = places.sortedBy { place ->
                    (place.latLng?.let { latLng ->
                        calculateDistance(center, latLng)
                    } ?: Double.MAX_VALUE) as Comparable<Any> // Handle null latLng gracefully
                }
                Log.i("Location", "SORTED")
                sortedPlaces.forEach{ place ->
                    place.displayName.let { Log.i( "Location", it.toString()) }
                }
                 */
                continuation.resume(places) {}
            }
            .addOnFailureListener { exception ->
                Log.e("Location", "Failed to retrieve places: ${exception.message}")
                continuation.resumeWith(Result.failure(exception)) // Resume with an exception
            }
        }
    } catch (e: Exception) {
        Log.e("Location", "Failed to retrieve places")
        throw Exception("Failed to retrieve places: ${e.message}")
    }
}

fun calculateDistance(from: LatLng, to: LatLng): Float {
    val results = FloatArray(1)
    android.location.Location.distanceBetween(
        from.latitude,
        from.longitude,
        to.latitude,
        to.longitude,
        results
    )

    val distance = results[0] // Distance in meters
    return distance
}
