package com.example.notes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notes.models.UserViewModel

@Composable
fun RegisterScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    var viewModel = UserViewModel()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(30.dp).padding(top = 50.dp),

    ) {

        Text(
            text= "Create an Account",
            modifier = Modifier.padding(bottom = 20.dp),
            style = MaterialTheme.typography.headlineMedium
        )

        // Name Input
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Enter your name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Username Input
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Enter your username") },
            modifier = Modifier.fillMaxWidth()
        )

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

        // Confirm Password Input
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm your password") },
            modifier = Modifier.fillMaxWidth()
        )

        if(password != confirmPassword){
            Text("Passwords do not match.")
        }

        if(isError){
            Text("All fields are required.")
        }

        // Submit Button
        Button(
            onClick = {
                // Handle form submission
                if(name.isEmpty() || email.isEmpty() || password.isEmpty() || username.isEmpty() || (password != confirmPassword)){
                    isError = true
                    println("All fields are required or passwords do not match.")
                    return@Button
                }
                else{
                    viewModel.saveUser(name, email, password, username){
                        navController.navigate("home")
                    }



                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            Text("Create Account")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Text("Already have an account?")
            TextButton(onClick = {
                navController.navigate("login")
            }) {
                Text(
                    text="Login here",
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )

            }
        }
    }
}

