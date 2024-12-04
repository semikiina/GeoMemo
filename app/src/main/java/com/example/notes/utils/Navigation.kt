package com.example.notes.utils

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notes.ui.screens.AuthenticationScreen
import com.example.notes.ui.screens.LoginScreen
import com.example.notes.ui.screens.MainScreen
import com.example.notes.ui.screens.NoteOverviewScreen
import com.example.notes.ui.screens.NoteScreen
import com.example.notes.ui.screens.NotesAtPlaceScreen
import com.example.notes.ui.screens.ProfileScreen
import com.example.notes.ui.screens.RegisterScreen

@Composable
fun Navigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Authentication.route) {

        composable(route = Screen.Authentication.route) {
            AuthenticationScreen(navController = navController)
        }

        composable(route = Screen.Register.route) {
           RegisterScreen(navController = navController)
        }

        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(route = Screen.Home.route) {
            MainScreen(navController = navController)
        }
        composable(route = Screen.Note.route) {
          NoteScreen(navController = navController)
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen(navController =navController)
        }
        composable(route = Screen.NoteOverview.route) {
            NoteOverviewScreen(navController=navController)
        }
        composable(route = Screen.NotesAtPlace.route) {
            val placeId = it.arguments?.getString("placeId")
            if (placeId != null) {
                NotesAtPlaceScreen(placeId = placeId, navController = navController)
            } else {
                Log.e("Location","Navigation: no placeId")
            }
        }
    }
}