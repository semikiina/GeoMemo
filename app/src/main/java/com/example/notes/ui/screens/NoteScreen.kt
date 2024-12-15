package com.example.notes.ui.screens

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.ui.draw.shadow
import com.example.notes.models.Note
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.notes.data.getNearestPlaces
import com.example.notes.models.UserViewModel
import com.google.android.libraries.places.api.model.AddressComponents
import com.google.android.libraries.places.api.model.Place

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(navController: NavController,  userViewModel: UserViewModel) {
    var noteText by remember { mutableStateOf("") }
    var selectedNoteType by remember { mutableStateOf("Daily Note") }
    var isPublic by remember { mutableStateOf(true) }
    val db = Firebase.firestore // Firebase Firestore-Referenz
    val maxNoteLength = 220 // max words

    var username by remember { mutableStateOf("Loading...") }


    // to access nearest place from current location
    val context = LocalContext.current
    val nearestPlace = remember { mutableStateOf(
        Place.builder()
            .setDisplayName("no name")
            .setId("no id")
            .setAdrFormatAddress("no address")
            .build()
    ) }
    LaunchedEffect(Unit) {
        //Log.i("Location", "Launched effect get nearest places")
        val places = getNearestPlaces(context)
        //Log.i("Location", "places count ${places.size}")
        nearestPlace.value = places[0]
        Log.i("Location", "nearest place ${nearestPlace.value.adrFormatAddress}")
    }

    LaunchedEffect(userViewModel.userUID.value) {
        val uid = userViewModel.userUID.value
        if (!uid.isNullOrBlank()) {
            userViewModel.loadUserProfile(uid) { user ->
                username = user.username
            }
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
            // Auswahl des Notiztyps, Public/Private Text und Schalter
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
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Blue,
                        ) ,
                    ) {
                        Text(
                            text=selectedNoteType,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        val noteTypes = listOf("Daily Note", "Weekly Note", "Monthly Note")
                        noteTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(
                                    text = type,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(vertical = 5.dp)
                                ) },
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
                    text = if (isPublic) "Public" else "Privat",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Switch(
                    checked = isPublic,
                    onCheckedChange = { isPublic = it },
                    modifier = Modifier.align(Alignment.CenterVertically),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Blue,
                        checkedTrackColor = Color.LightGray
                    )
                )
            }
            Text(
                text = "Your Location",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp),
                fontWeight = FontWeight.Bold,
                color = Color.Blue
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {

                        nearestPlace.value.displayName?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        nearestPlace.value.formattedAddress?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location Icon",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Eingabefeld für die Notiz
            Text(
                text = "Your Note",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp),
                fontWeight = FontWeight.Bold,
                color = Color.Blue
            )

            OutlinedTextField(
                value = noteText,
                onValueChange = { newText ->
                    // max 220 characters
                    if (newText.length <= maxNoteLength) {
                        noteText = newText
                    }
                },
                label = { Text("Write your note here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .shadow(elevation = 4.dp)
            )



            Text(
                text = "${maxNoteLength - noteText.length} characters remaining",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                modifier = Modifier.padding(top = 8.dp),
                color = if(noteText.length == maxNoteLength) Color.Red else Color.Gray
            )


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (noteText.isNotBlank()) {
                        val visibility = if (isPublic) "Public" else "Privat"
                        val noteDate = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(
                            Date()
                        )

                        val expirationTime = when (selectedNoteType) {
                            //"Daily Note" -> System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24 h
                            "Daily Note" -> System.currentTimeMillis() + (1 * 60 * 1000) // 1 Minute for testing
                            "Weekly Note" -> System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000) // 7 days
                            "Monthly Note" -> System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000) // 30 days
                            else -> System.currentTimeMillis()
                        }


                        val note = Note(
                            noteText = noteText,
                            type = selectedNoteType,
                            visibility = visibility,
                            uid = userViewModel.userUID.value ?: "",
                            username = username,
                            timestamp = System.currentTimeMillis(),
                            date = noteDate,
                            expirationTime = expirationTime,
                            placeName = nearestPlace.value.displayName ?: "Unknown",
                            placeId = nearestPlace.value.id ?: "Unknown",
                        )


                        db.collection("notes")
                            .add(note)
                            .addOnSuccessListener {
                                navController.navigate("home")
                            }
                            .addOnFailureListener { exception ->
                                println("Error saving note: $exception")
                            }

                        // Eingabe zurücksetzen
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