package com.example.notes.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteOverviewScreen(navController: NavController) {
    val db = Firebase.firestore
    var notes by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.collection("notes")
            .orderBy("timestamp", Query.Direction.DESCENDING) // optional: nach Zeitstempel sortieren
            .get()
            .addOnSuccessListener { result ->
                notes = result.documents.map { it.data ?: emptyMap() }
            }
            .addOnFailureListener { exception ->
                println("Error fetching notes: $exception")
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Note Overview") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(notes) { note ->
                val noteText = note["noteText"] as? String ?: "No Text"
                val type = note["type"] as? String ?: "Unknown"
                val visibility = note["visibility"] as? String ?: "Unknown"
                val username = note["username"] as? String ?: "Anonymous"
                val timestamp = note["timestamp"] as? Long ?: 0L

                // Format das Datum, wenn der Timestamp vorhanden ist
                val noteDate = if (timestamp != 0L) {
                    val date = Date(timestamp)
                    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
                    sdf.format(date)
                } else {
                    "No Date"
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = noteText, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Type: $type | $visibility",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                        Text(
                            text = "By: $username",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Date: $noteDate",  // Datum anzeigen
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                        }

                    }
                }
            }
        }
    }
}