package com.example.notes.ui.screens

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

    if (isLoading) {
        CircularProgressIndicator() // Show loading indicator
    } else {
        userData?.let { user ->
            UserInformation(user = user) // Display user information
        }
    }

    Text(text = "Main Screen")
}

@Composable
fun UserInformation(user : User){
    Text(text = "Welcome, ${user?.name}")
}