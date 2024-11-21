package com.example.notes.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

// Funktion zum Erzeugen eines zufälligen Strings im Format "abc123xyz456"
fun generateRandomString(): String {
    val letters = "abcdefghijklmnopqrstuvwxyz"
    val digits = "0123456789"

    val part1 = (1..3).map { letters.random() }.joinToString("")
    val part2 = (1..3).map { digits.random() }.joinToString("")
    val part3 = (1..3).map { letters.random() }.joinToString("")
    val part4 = (1..3).map { digits.random() }.joinToString("")

    return "$part1$part2$part3$part4"
}

// Funktion, um einen zufälligen Avatar zu generieren
fun generateRandomAvatar(): String {
    val randomString = generateRandomString() // Erzeuge einen zufälligen String im gewünschten Format
    return "https://api.multiavatar.com/$randomString.svg"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    // State für den Avatar-Link
    val avatarUrl = remember { mutableStateOf(generateRandomAvatar()) }

    // Logge die generierte URL
    Log.d("ProfileScreen", "Generated Avatar URL: ${avatarUrl.value}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(top = 32.dp), // Abstand von oben
            horizontalAlignment = Alignment.CenterHorizontally // Inhalte horizontal zentrieren
        ) {
            // Lade das SVG-Bild mit Coil-SVG
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatarUrl.value) // State-gesteuerte URL
                        .decoderFactory(SvgDecoder.Factory()) // SVG-Support aktivieren
                        .build()
                ),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp) // Größe des Bildes
                    .padding(bottom = 16.dp)
                    .clip(CircleShape) // Kreisform
            )

            // Name Text
            Text(
                text = "Cristina Semikina",
                style = MaterialTheme.typography.headlineMedium // Aktualisierter Stil für Text
            )

            // Handle Text
            Text(
                text = "@crislinda",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button oder andere Inhalte
            Button(onClick = {
                avatarUrl.value = generateRandomAvatar() // Neue URL generieren und setzen
            }) {
                Text("Generate New Avatar")
            }
        }
    }
}
