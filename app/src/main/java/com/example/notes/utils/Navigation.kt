package com.example.notes.utils

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notes.models.UserViewModel
import com.example.notes.ui.screens.AuthenticationScreen
import com.example.notes.ui.screens.LoginScreen
import com.example.notes.ui.screens.MainScreen
import com.example.notes.ui.screens.NoteOverviewScreen
import com.example.notes.ui.screens.NoteScreen
import com.example.notes.ui.screens.ProfileScreen
import com.example.notes.ui.screens.RegisterScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel() // ViewModel wird hier erstellt

    NavHost(navController = navController, startDestination = Screen.Login.route) {

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

        // ProfileScreen: Übergabe des ViewModels
        composable(route = Screen.Profile.route) {
            ProfileScreen(navController = navController, viewModel = userViewModel)
        }

        composable(route = Screen.NoteOverview.route) {
            NoteOverviewScreen(navController = navController)
        }
    }
}
