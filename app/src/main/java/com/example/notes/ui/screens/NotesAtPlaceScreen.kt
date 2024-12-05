package com.example.notes.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.notes.models.Note
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesAtPlaceScreen(placeId: String, navController: NavController) {

    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val db = Firebase.firestore
    db.collection("notes")
        .whereEqualTo("placeId", placeId)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                /*
                for (document in documents) {
                    val note = document.toObject(Note::class.java)
                    notes.toMutableList().add(note)
                    Log.i("Location","Retrieved note: ${note.noteText}")
                }
                 */
                notes = documents.map { document ->
                    document.toObject(Note::class.java)
                }
            } else {
                Log.i("Location","No notes found for placeId: $placeId")
            }
            isLoading = false
        }
        .addOnFailureListener { exception ->
            Log.i("Location","Error retrieving notes: $exception")
            isLoading = false
        }
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text ("Notes at place")},
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No notes available.")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(notes) { note ->
                    NoteCard(note = note)
                }
            }
        }
    }
}