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
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.notes.data.getNearestPlaces
import com.google.android.libraries.places.api.model.Place

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(navController: NavController) {
    var noteText by remember { mutableStateOf("") }
    var selectedNoteType by remember { mutableStateOf("Daily Note") }
    var isPublic by remember { mutableStateOf(true) }
    val username = "Cristina Semikina" // Beispiel-Benutzername
    val db = Firebase.firestore // Firebase Firestore-Referenz
    val maxNoteLength = 220 // max words

    // to access nearest place from current location
    val context = LocalContext.current
    val nearestPlace = remember { mutableStateOf(
        Place.builder()
            .setDisplayName("no name")
            .setId("no id")
            .build()
    ) }
    LaunchedEffect(Unit) {
        //Log.i("Location", "Launched effect get nearest places")
        val places = getNearestPlaces(context)
        //Log.i("Location", "places count ${places.size}")
        nearestPlace.value = places[0]
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
                    text = if (isPublic) "Public" else "Privat",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Switch(
                    checked = isPublic,
                    onCheckedChange = { isPublic = it },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
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
                            text = "location",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = nearestPlace.value.displayName.toString(),
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Text(
                            text = nearestPlace.value.id.toString(),
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

            // Eingabefeld für die Notiz
            Text(
                text = "Your Note",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
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
            )


            // show how many characters are left
            Text(
                text = "${maxNoteLength - noteText.length} characters remaining",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                modifier = Modifier.padding(top = 8.dp)
            )

            // Button zum Erstellen der Notiz
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (noteText.isNotBlank()) {
                        val visibility = if (isPublic) "Public" else "Privat"
                        //val currentDate = java.time.LocalDateTime.now() // Aktuelles Datum und Uhrzeit
                        //val formattedDate = currentDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        val noteDate = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(
                            Date()
                        )


                        // Notiz in Firestore speichern
                        val noteData = hashMapOf(
                            "noteText" to noteText,
                            "type" to selectedNoteType,
                            "visibility" to visibility,
                            "username" to username,
                            "timestamp" to System.currentTimeMillis(),
                            "date" to noteDate  // save date as string
                        )

                        db.collection("notes")
                            .add(noteData)
                            .addOnSuccessListener {
                                navController.navigate("noteOverview")
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