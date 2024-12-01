package com.example.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.notes.ui.theme.NotesTheme
import com.example.notes.utils.Navigation
import androidx.work.*
import com.example.notes.workers.NoteCleanupWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesTheme {
                Navigation()
            }
        }
        setupNoteCleanupWorker()
    }
    private fun setupNoteCleanupWorker() {

        val workRequest = PeriodicWorkRequestBuilder<NoteCleanupWorker>(24, TimeUnit.HOURS) // Alle 24 Stunden
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()


        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "NoteCleanupWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
