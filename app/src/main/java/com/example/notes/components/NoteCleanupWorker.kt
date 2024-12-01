package com.example.notes.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class NoteCleanupWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val db = FirebaseFirestore.getInstance()
        val currentTime = System.currentTimeMillis()

        return try {
            // Abrufen aller abgelaufenen Notizen
            val snapshot = db.collection("notes")
                .whereLessThanOrEqualTo("expirationTime", currentTime)
                .get()
                .await() // Suspendierende Funktion

            // Löschen aller abgelaufenen Notizen
            val batch = db.batch() // Batch-Operation für effizienteres Löschen
            snapshot.documents.forEach { document ->
                batch.delete(document.reference)
            }
            batch.commit().await() // Batch ausführen

            // Erfolg melden
            println("Successfully deleted ${snapshot.size()} expired notes from Firestore.")
            Result.success()
        } catch (e: Exception) {
            // Fehlerbehandlung
            e.printStackTrace()
            println("Error deleting expired notes: $e")
            Result.retry() // Job erneut ausführen, wenn ein Fehler auftritt
        }
    }
}
