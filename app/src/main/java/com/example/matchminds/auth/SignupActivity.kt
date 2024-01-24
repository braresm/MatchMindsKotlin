package com.example.matchminds.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.matchminds.profile.ProfileActivity
import com.example.matchminds.R
import com.example.matchminds.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class SignupActivity : AppCompatActivity() {

    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Display the "back" button
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // Change the title using title property
        title = "Sign up"

        val authRepository = AuthRepository()

        val signupButton = findViewById<Button>(R.id.signupButton)
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val studentIdEditText = findViewById<EditText>(R.id.studentIdEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)


        // Set a click listener for the signup button
        signupButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val studentId = studentIdEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(applicationContext, "Please add a name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (studentId.isEmpty()) {
                Toast.makeText(applicationContext, "Please add a student ID", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(applicationContext, "Please add a password", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            registerUser(
                authRepository, studentId, name, password
            )
        }
    }

    private fun registerUser(
        repository: AuthRepository,
        studentId: String,
        name: String,
        password: String
    ) {
        repository.createUserWithEmailAndPassword(studentId, password) { user, error ->
            if (user != null) {
                updateDisplayName(user, name)
            } else {
                Toast.makeText(applicationContext, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateDisplayName(user: FirebaseUser, name: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { profileUpdateTask ->
                if (profileUpdateTask.isSuccessful) {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Update display name failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // Handle the up navigation action here
            // For example, you can finish() to go back to the parent activity
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}