package com.example.notes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notes.models.UserViewModel

@Composable
fun LoginScreen(navController: NavController){

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var viewModel = UserViewModel()

    Column (
        modifier = Modifier.padding(20.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your email") },
            modifier = Modifier.fillMaxWidth()
        )

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter your password") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text="Forgot your password?",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )

        // Submit Button
        Button(
            onClick = {
                  viewModel.loginUser(email, password){
                      navController.navigate("home")
                  }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            Text("Login")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Text("Don't have an account?")
            TextButton(onClick = {
                navController.navigate("register")
            }) {
                Text(
                    text="Register here",
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )

            }
        }
    }
}