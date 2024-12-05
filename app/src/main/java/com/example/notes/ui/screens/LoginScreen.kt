package com.example.notes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notes.models.UserViewModel
import android.util.Log

@Composable
fun LoginScreen(navController: NavController, viewModel: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter your password") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Forgot your password?",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )

        Button(
            onClick = {
                isLoading = true // Ladeanzeige aktivieren
                viewModel.loginUser(email, password) {
                    Log.d("LoginScreen", "Login successful. Waiting for userUID...")
                    isLoading = false
                    navController.navigate("home") // Navigiere zur Profilseite
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login")
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Don't have an account?")
            TextButton(onClick = {
                navController.navigate("register")
            }) {
                Text(
                    text = "Register here",
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
