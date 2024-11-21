package com.example.notes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(navController: NavController) {
    var noteText by remember { mutableStateOf("") } // Eingabe für Notizen
    var notesList by remember { mutableStateOf(listOf<Pair<String, String>>()) } // Liste der Notizen mit Benutzername
    var selectedNoteType by remember { mutableStateOf("Daily Note") } // Ausgewählter Notiztyp
    var isPublic by remember { mutableStateOf(true) } // Schalter für Public/Privat (Standard: Public)
    val username = "Cristina Semikina" // Beispiel-Benutzername

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
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
            // Auswahl des Notiztyps, Public/Private Text und Schalter in einer Zeile
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Abstand zwischen den Elementen
            ) {
                // Dropdown-Menü für Notiztypen
                var expanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(1f)) { // Damit das Dropdown und die anderen Elemente ordentlich nebeneinander passen
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

                // Füge hier einen kleinen Abstand zwischen Dropdown und Public/Private Schalter ein
                Spacer(modifier = Modifier.width(8.dp))

                // Text "Public/Private"
                Text(
                    text = if (isPublic) "Public" else "Privat",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                // Schalter für Public/Privat
                Switch(
                    checked = isPublic,
                    onCheckedChange = { isPublic = it },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            // "Your Location" Bereich
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
                            text = "No location selected",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Please enable location services",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location Icon",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Eingabefeld für die Notiz (größer)
            Text(
                text = "Your Note",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Write your note here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Vergrößerung des Textfelds
            )

            // Button unter dem Notizfeld
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (noteText.isNotBlank()) {
                        val visibility = if (isPublic) "Public" else "Privat"
                        notesList = notesList + Pair("$selectedNoteType ($visibility): $noteText", username)
                        noteText = "" // Eingabe zurücksetzen
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Create Note")
            }

            // Anzeige der Notizen
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(notesList) { (note, user) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = note,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "By $user",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                            )
                        }
                    }
                }
            }
        }
    }
}
