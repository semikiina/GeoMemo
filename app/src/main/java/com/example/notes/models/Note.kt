package com.example.notes.models

data class Note(
    val id: String = "",
    val noteText: String = "",
    val type: String = "",
    val visibility: String = "",
    val username: String = "",
    val timestamp: Long = 0L,
    val date: String = "",
    val expirationTime: Long = 0L,
    val placeName: String = "",
    val placeId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {

}