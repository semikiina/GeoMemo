package com.example.notes.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
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
import com.example.notes.models.UserViewModel
import androidx.compose.runtime.livedata.observeAsState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: UserViewModel = UserViewModel()) {
    // States für Benutzerinformationen
    val userUID by viewModel.userUID.observeAsState(initial = null)
    var avatarUrl by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    // Daten von Firebase laden
    LaunchedEffect(userUID) {
        userUID?.let { uid ->
            viewModel.loadUserProfile(uid) { userProfile ->
                avatarUrl = userProfile.avatarUrl
                name = userProfile.name
                username = userProfile.username
            }
        }
    }

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
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Lade das gespeicherte SVG-Bild
            if (avatarUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(avatarUrl)
                            .decoderFactory(SvgDecoder.Factory())
                            .build()
                    ),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .padding(bottom = 16.dp)
                )
            }

            // Benutzername
            if (name.isNotEmpty()) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            // Username
            if (username.isNotEmpty()) {
                Text(
                    text = "@$username",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
