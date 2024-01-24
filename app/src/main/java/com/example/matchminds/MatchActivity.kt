package com.example.matchminds

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import com.example.matchminds.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MatchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)

        title = "Match"

        val match1 = findViewById<LinearLayout>(R.id.matchContainer1)
        val match2 = findViewById<LinearLayout>(R.id.matchContainer2)

        match1.setOnClickListener(){
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        match2.setOnClickListener(){
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.action_match

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_swipe -> {
                    val intent = Intent(this, SwipeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_match -> true
                R.id.action_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}