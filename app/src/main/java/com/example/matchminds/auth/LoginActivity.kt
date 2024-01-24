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

class LoginActivity : AppCompatActivity() {
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Display the "back" button
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // Change the title using title property
        title = "Login"

        val authRepository = AuthRepository()

        val loginButton = findViewById<Button>(R.id.loginButton)
        val studentIdEditText = findViewById<EditText>(R.id.studentIdEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        // Set a click listener for the login button
        loginButton.setOnClickListener {
            val studentId = studentIdEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

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

            loginUser(authRepository, studentId, password)
        }
    }

    private fun loginUser(repository: AuthRepository, studentId: String, password: String) {
        repository.signInWithEmailAndPassword(studentId, password) { user, error ->
            if (user != null) {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, error, Toast.LENGTH_SHORT).show()
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