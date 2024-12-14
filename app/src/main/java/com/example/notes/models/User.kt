package com.example.notes.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Datenklasse für den Benutzer
data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val username: String = "",
    val avatarUrl: String = ""
)

class UserViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val _userUID = MutableLiveData<String?>()
    val userUID: LiveData<String?> = _userUID

    init {
        // Initialisiere userUID mit dem aktuell angemeldeten Benutzer
        val currentUser = auth.currentUser
        if (currentUser != null) {
            setUserUID(currentUser.uid)
        }
    }

    fun setUserUID(uid: String?) {
        Log.d("UserViewModel", "Setting userUID: $uid")
        _userUID.value = uid
    }

    fun loginUser(email: String, password: String, onLoginCompleted: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid
                    Log.d("UserViewModel", "User logged in: UID=$uid")
                    setUserUID(uid) // Aktualisiere die `userUID`
                    onLoginCompleted() // Navigation auslösen
                } else {
                    Log.e("UserViewModel", "Login failed: ${task.exception?.message}")
                }
            }
    }


    fun saveUser(
        name: String,
        email: String,
        password: String,
        username: String,
        avatarUrl: String,
        onSaveCompleted: () -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid ?: ""
                    val newUser = User(uid, email, name, username, avatarUrl)

                    Log.d("UserViewModel", "Saving user: UID=$uid, Name=$name, Username=$username")

                    db.collection("users")
                        .document(uid)
                        .set(newUser)
                        .addOnSuccessListener {
                            Log.d("UserViewModel", "User data saved successfully: UID=$uid")
                            setUserUID(uid) // Aktualisiere die UID des Benutzers
                            onSaveCompleted() // Rufe den Callback auf
                        }
                        .addOnFailureListener { exception ->
                            Log.e("UserViewModel", "Error saving user data: $exception")
                        }
                } else {
                    Log.e("UserViewModel", "Error creating user: ${task.exception?.message}")
                }
            }
    }

    fun loadUserProfile(uid: String, onUserLoaded: (User) -> Unit) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    Log.d("UserViewModel", "User profile loaded: $user")
                    onUserLoaded(user)
                } else {
                    Log.e("UserViewModel", "User profile not found for UID=$uid")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("UserViewModel", "Error loading user profile: $exception")
            }
    }

    fun getUser(uid: String, onUserLoaded: (User) -> Unit) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    onUserLoaded(user)
                }
            }
    }

    fun getUserNotes(uid: String, onNotesLoaded: (List<Note>) -> Unit) {
        db.collection("notes")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { documents ->
                val notes = documents.mapNotNull { it.toObject(Note::class.java) }
                Log.d("UserViewModel", "User notes loaded: $notes")
                onNotesLoaded(notes)
            }

    }
}
