package com.example.notes.utils

sealed class Screen (val route : String){
    object Authentication : Screen("authentication")
    object Register : Screen("register")
    object Login : Screen("login")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Settings : Screen("settings")

}