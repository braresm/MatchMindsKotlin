package com.example.matchminds.repository

import com.example.matchminds.models.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser;

    fun createUserWithEmailAndPassword(studentId: String, password: String, callback: (FirebaseUser?, String?) -> Unit) {
        // create the email based on the student id
        val email = "$studentId@customdomain.com"

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    callback(user, null) // User creation successful
                } else {
                    val exception = task.exception
                    val errorMessage = exception?.message ?: "User creation failed"
                    callback(null, errorMessage) // User creation failed
                }
            }
    }
    fun signInWithEmailAndPassword(studentId: String, password: String, callback: (FirebaseUser?, String?) -> Unit) {
        // create the email based on the student id
        val email = "$studentId@customdomain.com"

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    callback(user, null) // Authentication successful
                } else {
                    val exception = task.exception
                    val errorMessage = exception?.message ?: "Authentication failed"
                    callback(null, errorMessage) // Authentication failed
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun firebaseUser(): FirebaseUser? {
        return currentUser
    }

    fun getCurrentAccount(): Account? {
        if(currentUser != null) {
            val displayName = currentUser.displayName
            val studentId = currentUser.email?.split('@')?.get(0)

            if (displayName != null && studentId != null) {
                return Account(currentUser.uid, displayName, studentId)
            }
        }
        return null
    }
}