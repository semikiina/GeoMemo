package com.example.notes.models

data class Note(
    val noteText: String,
    val type: String,
    val visibility: String,
    val username: String,
    val timestamp: Long,
    val date: String
)
