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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.notes.components.NoteCard
import com.example.notes.models.Note
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesAtPlaceScreen(placeId: String, navController: NavController) {

    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val db = Firebase.firestore

    suspend fun deleteExpiredNotes(loadedNotes: List<Note>) {
        val currentTime = System.currentTimeMillis()
        val expiredNotes = loadedNotes.filter { it.expirationTime < currentTime }

        expiredNotes.forEach { note ->
            try {
                db.collection("notes").document(note.placeId).delete().await()
                Log.i("NotesAtPlaceScreen", "Deleted expired note with placeId: ${note.placeId}")
            } catch (e: Exception) {
                Log.e("NotesAtPlaceScreen", "Error deleting expired note: ${e.localizedMessage}")
            }
        }
    }

    LaunchedEffect(placeId) {
        isLoading = true

        db.collection("notes")
            .whereEqualTo("placeId", placeId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("NotesAtPlaceScreen", "Error loading notes: $exception")
                    isLoading = false
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val loadedNotes = snapshot.documents.mapNotNull { it.toObject(Note::class.java) }


                    kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                        deleteExpiredNotes(loadedNotes)
                    }

                    notes = loadedNotes.filter { it.expirationTime >= System.currentTimeMillis() }
                    Log.i("NotesAtPlaceScreen", "Loaded ${notes.size} valid notes")
                } else {
                    notes = emptyList()
                    Log.i("NotesAtPlaceScreen", "No notes found for placeId: $placeId")
                }
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes at place") },
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
                Text("No notes at this place.")
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
