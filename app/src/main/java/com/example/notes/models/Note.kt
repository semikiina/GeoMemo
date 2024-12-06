package com.example.notes.models

data class Note(
    val noteText: String = "",
    val type: String = "",
    val visibility: String = "",
    val username: String = "",
    val timestamp: Long = 0L,
    val date: String = "",
    val expirationTime: Long = 0L,
    val placeName: String = "",
    val placeId: String = "",
) {

}