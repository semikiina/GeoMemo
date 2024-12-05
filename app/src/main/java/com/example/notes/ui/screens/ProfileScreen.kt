// Updated ProfileScreen.kt file
package com.example.notes.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
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
    val userUID by viewModel.userUID.observeAsState(initial = null)
    var avatarUrl by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    LaunchedEffect(userUID) {
        Log.d("ProfileScreen", "Received userUID: $userUID")
        val currentUID = userUID // Create a local copy
        if (currentUID != null) { // Use the local copy
            Log.d("ProfileScreen", "Current userUID: $currentUID")
            viewModel.loadUserProfile(currentUID) { userProfile ->
                Log.d("ProfileScreen", "Loaded user profile: $userProfile")
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

            if (name.isNotEmpty()) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

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
