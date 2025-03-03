// Updated ProfileScreen.kt file
package com.example.notes.ui.screens

import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import com.example.notes.models.UserViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.notes.components.NoteCard
import com.example.notes.models.Note

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: UserViewModel = UserViewModel()) {
    val userUID by viewModel.userUID.observeAsState(initial = null)
    var avatarUrl by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var userNotes by remember { mutableStateOf<List<Note>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

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
                isLoading = false
            }
            viewModel.getUserNotes(currentUID) { notes ->
                userNotes = notes
            }
        }
    }
    // Conditional rendering
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        } // Display loading indicator
    } else {
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
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = 32.dp)
                    .padding(horizontal = 16.dp),

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
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                if (username.isNotEmpty()) {
                    Text(
                        text = "@$username",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "Lorem ipsum dolor sit amet consectetur. Tellus viverra lacus erat habitant ut mollis. Nunc leo adipiscing quis nisi magna vitae facilisi. ",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "${userNotes.size} Notes created",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp),
                    fontWeight = FontWeight.Bold
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "Your Private Notes",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    LazyColumn {
                        items(userNotes) { note ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NoteCard(note, navController)
                            }
                        }
                    }
                }
            }
        }
    }
}


