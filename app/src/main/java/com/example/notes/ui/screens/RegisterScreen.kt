package com.example.notes.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.notes.models.UserViewModel
import android.util.Log

fun generateRandomAvatar(): String {
    val randomString = (1..12).map { ('a'..'z').random() }.joinToString("")
    return "https://api.multiavatar.com/$randomString.svg"
}

@Composable
fun RegisterScreen(navController: NavController, viewModel: UserViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    val avatarUrl = remember { mutableStateOf(generateRandomAvatar()) }
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .padding(30.dp)
            .padding(top = 50.dp)
    ) {
        Text(
            text = "Create an Account",
            modifier = Modifier.padding(bottom = 20.dp),
            style = MaterialTheme.typography.headlineMedium
        )

        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatarUrl.value)
                    .decoderFactory(SvgDecoder.Factory())
                    .build()
            ),
            contentDescription = "Generated Avatar",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .padding(bottom = 16.dp)
        )

        Button(onClick = { avatarUrl.value = generateRandomAvatar() }) {
            Text("Generate New Avatar")
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Enter your name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Enter your username") },
            modifier = Modifier.fillMaxWidth()
        )

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

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm your password") },
            modifier = Modifier.fillMaxWidth()
        )

        if (password != confirmPassword) {
            Text("Passwords do not match.", color = MaterialTheme.colorScheme.error)
        }

        if (isError) {
            Text("All fields are required.", color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || username.isEmpty() || password != confirmPassword) {
                    isError = true
                    Log.e("RegisterScreen", "Validation failed: Name=$name, Email=$email, Username=$username")
                } else {
                    isLoading = true
                    viewModel.saveUser(name, email, password, username, avatarUrl.value) {
                        isLoading = false
                        navController.navigate("home") // Nach erfolgreicher Registrierung zur Login-Seite
                    }
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
                Text("Create Account")
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Already have an account?")
            TextButton(onClick = {
                navController.navigate("login")
            }) {
                Text(
                    text = "Login here",
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
