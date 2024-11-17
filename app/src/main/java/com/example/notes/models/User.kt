package com.example.notes.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val username: String = ""
)

class UserViewModel : ViewModel(){

    val auth = Firebase.auth
    val db = Firebase.firestore

    private val _userUID = MutableLiveData<String?>()
    val userUID: LiveData<String?> = _userUID

    fun setUserUID(uid: String?) {
        _userUID.value = uid
    }

    fun getUserUID(): String? {
        return _userUID.value
    }

    fun saveUser(name: String, email: String, password: String, username: String, onSaveCompleted: () -> Unit) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid ?: ""
                    val newUser = User(uid, email, name, username)

                    db.collection("users")
                        .document(uid)
                        .set(newUser)
                        .addOnSuccessListener {
                            // User data saved successfully
                            println("User data saved successfully")
                            setUserUID(uid)
                            onSaveCompleted()
                        }
                        .addOnFailureListener { exception ->
                            // Handle error
                            println("Error saving user data: $exception")
                        }
                } else {
                    // Handle error
                    println("Error creating user: ${task.exception}")
                }
            }
    }

    fun loginUser(email : String, password : String, onSaveCompleted: () -> Unit) {
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

}