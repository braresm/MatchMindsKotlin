package com.example.matchminds

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBar
import com.example.matchminds.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Change the title using title property
        title = "Chat"

        // Display the "back" button
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val meetButton = findViewById<Button>(R.id.buttonMeet)

        meetButton.setOnClickListener(){
            val intent = Intent(this, MeetActivity::class.java)
            startActivity(intent)
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