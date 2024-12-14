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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign

fun generateRandomAvatar(): String {
    val randomString = (1..12).map { ('a'..'z').random() }.joinToString("")
    return "https://api.multiavatar.com/$randomString.svg"
}

@OptIn(ExperimentalMaterial3Api::class)
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



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text="Create Account",
                        textAlign = TextAlign.Center
                    )
                        },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },

                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { paddingValues ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(paddingValues)
                .padding(30.dp)
        ) {


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

            Button(
                onClick = { avatarUrl.value = generateRandomAvatar() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDCF91B),
                    contentColor = Color.Black,
                ),
            ) {
                Text(
                    text = "Generate New Avatar",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Enter your name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Enter your username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter your email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Enter your password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm your password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
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
                        Log.e(
                            "RegisterScreen",
                            "Validation failed: Name=$name, Email=$email, Username=$username"
                        )
                    } else {
                        isLoading = true
                        viewModel.saveUser(name, email, password, username, avatarUrl.value) {
                            isLoading = false
                            navController.navigate("profile") // Nach erfolgreicher Registrierung zur Login-Seite
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDCF91B),
                    contentColor = Color.Black,
                ),
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
                    Text(
                        text = "Create Account",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text="Already have an account?",
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = {
                    navController.navigate("login")
                }) {
                    Text(
                        text = "Login here",
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold,
                        style= MaterialTheme.typography.bodyLarge,
                        color = Color.Blue
                    )
                }
            }
        }
    }
}
