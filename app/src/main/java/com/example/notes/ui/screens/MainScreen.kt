package com.example.notes.ui.screens

import android.util.Log
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.notes.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notes.components.BottomNavigationBar
import com.example.notes.components.MainScreenMap
import com.example.notes.utils.Screen

@Composable
fun MainScreen(navController: NavController) {
    val auth = Firebase.auth
    val db = Firebase.firestore

    val user = auth.currentUser
    val uid = user?.uid ?: ""

    var userData by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(uid) { // Trigger when uid changes
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    userData = documentSnapshot.toObject(User::class.java)
                } else {
                    println("User document does not exist")
                }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                println("Error getting user document: $exception")
                isLoading = false
            }
    }

    Scaffold(
        bottomBar = {
            // BottomNavigationBar only shown on home screen
            if (navController.currentBackStackEntry?.destination?.route == Screen.Home.route) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        // Content below the BottomNavigationBar
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(paddingValues))
        } else {
            userData?.let { user ->
                UserInformation(user = user) // Display user information
            }
        }

        MainScreenMap()
    }

}

@Composable
fun UserInformation(user : User){
    Text(text = "Welcome, ${user?.name}")
}