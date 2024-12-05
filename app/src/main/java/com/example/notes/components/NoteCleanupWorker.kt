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
            // Abgelaufene Notizen abrufen
            val snapshot = db.collection("notes")
                .whereLessThanOrEqualTo("expirationTime", currentTime)
                .get()
                .await()

            if (snapshot.isEmpty) {
                println("No expired notes found to delete.")
                return Result.success()
            }

            // Löschen der abgelaufenen Notizen
            val batch = db.batch()
            snapshot.documents.forEach { document ->
                batch.delete(document.reference)
            }
            batch.commit().await()

            println("Successfully deleted ${snapshot.size()} expired notes from Firestore.")
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error deleting expired notes: $e")
            Result.retry() // Wiederholen bei Fehler
        }
    }
}
