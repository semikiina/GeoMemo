package com.example.notes.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
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
import androidx.compose.ui.Alignment
import com.example.notes.components.NoteCard
import com.example.notes.models.Note
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteOverviewScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) } // Ladezustand für bessere Benutzerfreundlichkeit

    LaunchedEffect(Unit) {
        loadNotes(db) { loadedNotes, error ->
            if (error != null) {
                println("Error fetching notes: $error")
            } else {
                notes = loadedNotes ?: emptyList()
            }
            isLoading = false
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
        if (isLoading) {
            // Ladeanzeige während des Datenabrufs
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (notes.isEmpty()) {
            // Nachricht, wenn keine Notizen verfügbar sind
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No notes available.")
            }
        } else {
            // Liste der Notizen anzeigen
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(notes) { note ->
                    NoteCard(note = note)
                }
            }
        }
    }
}


suspend fun loadNotes(
    db: FirebaseFirestore,
    callback: (List<Note>?, Exception?) -> Unit
) {
    try {
        val snapshot = withContext(Dispatchers.IO) {
            db.collection("notes")
                .whereGreaterThan("expirationTime", System.currentTimeMillis())
                .orderBy("expirationTime", Query.Direction.ASCENDING)
                .get()
                .await()
        }

        val notes = snapshot.documents.mapNotNull { it.toObject(Note::class.java) }
        callback(notes, null)
    } catch (e: Exception) {
        callback(null, e)
    }
}
