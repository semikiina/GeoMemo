package com.example.notes.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// User-Datenklasse mit Avatar-URL
data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val username: String = "",
    val avatarUrl: String = "" // Neues Feld für Avatar-URL
)

class UserViewModel : ViewModel() {

    val auth = Firebase.auth
    val db = Firebase.firestore

    private val _userUID = MutableLiveData<String?>()
    val userUID: LiveData<String?> = _userUID

    fun setUserUID(uid: String?) {
        Log.d("UserViewModel", "Setting userUID: $uid")
        _userUID.value = uid
    }

    fun getUserUID(): String? {
        return _userUID.value
    }

    // Aktualisierte saveUser-Methode mit Avatar-URL
    fun saveUser(name: String, email: String, password: String, username: String, avatarUrl: String, onSaveCompleted: () -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid ?: ""
                    val newUser = User(uid, email, name, username, avatarUrl) // Avatar-URL in User-Objekt

                    db.collection("users")
                        .document(uid)
                        .set(newUser)
                        .addOnSuccessListener {
                            // User-Daten erfolgreich gespeichert
                            println("User data saved successfully")
                            setUserUID(uid)
                            onSaveCompleted()
                        }
                        .addOnFailureListener { exception ->
                            // Fehler beim Speichern der Daten
                            println("Error saving user data: $exception")
                        }
                } else {
                    // Fehler bei der Benutzererstellung
                    println("Error creating user: ${task.exception}")
                }
            }
    }

    fun loginUser(email: String, password: String, onSaveCompleted: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Login successful")
                    val user = auth.currentUser
                    val uid = user?.uid ?: ""
                    setUserUID(uid)
                    onSaveCompleted()
                } else {
                    println("Login failed: ${task.exception}")
                }
            }
    }
    fun loadUserProfile(uid: String, onUserLoaded: (User) -> Unit) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)
                    user?.let {
                        onUserLoaded(it)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileScreen", "Error loading user profile: $exception")
            }
    }

}
