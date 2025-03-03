package com.example.notes.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen (val route : String){
    object Authentication : Screen("authentication")
    object Register : Screen("register")
    object Login : Screen("login")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Note: Screen("note")
    object NoteOverview: Screen("noteOverview")

    object NotesAtPlace : Screen("notesAtPlace/{placeId}") {
        fun createRoute(placeId: String): String {
            return "notesAtPlace/$placeId"
        }
    }

    object UserProfile : Screen("userProfile/{userId}") {
        fun createRoute(userId: String): String {
            return "userProfile/$userId"
        }
    }
}