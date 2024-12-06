package com.example.notes.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import com.example.notes.models.Note
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.notes.data.getNearestPlaces
import com.example.notes.models.UserViewModel
import com.google.android.libraries.places.api.model.Place

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(navController: NavController, userViewModel: UserViewModel) {
    var noteText by remember { mutableStateOf("") }
    var selectedNoteType by remember { mutableStateOf("Daily Note") }
    var isPublic by remember { mutableStateOf(true) }
    val db = Firebase.firestore // Firebase Firestore reference
    val maxNoteLength = 220 // Max allowed characters

    // State to hold the username
    var username by remember { mutableStateOf("Loading...") }

    // State for the nearest location
    val context = LocalContext.current
    val nearestPlace = remember {
        mutableStateOf(
            Place.builder()
                .setDisplayName("no name")
                .setId("no id")
                .build()
        )
    }

    // Load the username using the UserViewModel
    LaunchedEffect(userViewModel.userUID.value) {
        val uid = userViewModel.userUID.value
        if (!uid.isNullOrBlank()) {
            userViewModel.loadUserProfile(uid) { user ->
                username = user.username
            }
        }
    }

    // Load the nearest place
    LaunchedEffect(Unit) {
        val places = getNearestPlaces(context)
        if (places.isNotEmpty()) {
            nearestPlace.value = places[0]
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Note") },
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Note type selection and visibility toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                var expanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedNoteType)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        val noteTypes = listOf("Daily Note", "Weekly Note", "Monthly Note")
                        noteTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedNoteType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isPublic) "Public" else "Private",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Switch(
                    checked = isPublic,
                    onCheckedChange = { isPublic = it },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            // Location display
            Text(
                text = "Your Location",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Location",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = nearestPlace.value.displayName ?: "Unknown",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Text(
                            text = nearestPlace.value.id ?: "Unknown",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location Icon",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Note input field
            Text(
                text = "Your Note",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = noteText,
                onValueChange = { newText ->
                    if (newText.length <= maxNoteLength) {
                        noteText = newText
                    }
                },
                label = { Text("Write your note here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Text(
                text = "${maxNoteLength - noteText.length} characters remaining",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (noteText.isNotBlank()) {
                        val visibility = if (isPublic) "Public" else "Private"
                        val noteDate = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date())

                        val expirationTime = when (selectedNoteType) {
                            "Daily Note" -> System.currentTimeMillis() + (24 * 60 * 60 * 1000)
                            "Weekly Note" -> System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)
                            "Monthly Note" -> System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000)
                            else -> System.currentTimeMillis()
                        }

                        // Note object with username and location included
                        val note = Note(
                            noteText = noteText,
                            type = selectedNoteType,
                            visibility = visibility,
                            username = username, // Use the username loaded from Firestore
                            timestamp = System.currentTimeMillis(),
                            date = noteDate,
                            expirationTime = expirationTime,
                            placeName = nearestPlace.value.displayName ?: "Unknown",
                            placeId = nearestPlace.value.id ?: "Unknown",
                            //latitude = nearestPlace.value.latLng?.latitude ?: 0.0,
                            //longitude = nearestPlace.value.latLng?.longitude ?: 0.0
                        )

                        db.collection("notes")
                            .add(note)
                            .addOnSuccessListener {
                                navController.navigate("home")
                            }
                            .addOnFailureListener { exception ->
                                println("Error saving note: $exception")
                            }

                        noteText = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Create Note")
            }
        }
    }
}
