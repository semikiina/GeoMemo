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
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import coil.compose.AsyncImage
import com.example.notes.R
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ){
            Image(
                painter = painterResource(id = R.drawable.login),
                contentDescription = "Notes Logo",
                modifier = Modifier
                    .width(500.dp)
            )
            Card(
                shape = RoundedCornerShape(50.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(700.dp)
            ){
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,

                    ) {

                    Text(
                        text = "Welcome Back 👋🏻",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Enter your credentials to login to your account",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = Color.Gray
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
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Text(
                        text = "Forgot your password?",
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        textAlign = TextAlign.End,
                        color = Color.Blue,

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
                            .padding(top = 20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDCF91B),
                            contentColor = Color.Black,
                        ),
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Login",
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
                        Text(text="Don't have an account?", fontWeight = FontWeight.Bold)
                        TextButton(onClick = {
                            navController.navigate("register")
                        }) {
                            Text(
                                text = "Register here",
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
    }
}
