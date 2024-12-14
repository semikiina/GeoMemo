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
fun UserProfileScreen(navController: NavController, userId: String) {
    var isLoading by remember { mutableStateOf(true) }
    var avatarUrl by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var viewModel = UserViewModel()


    viewModel.loadUserProfile(userId) { userProfile ->
        avatarUrl = userProfile.avatarUrl
        name = userProfile.name
        username = userProfile.username
        isLoading = false
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

            }
        }
    }
}


