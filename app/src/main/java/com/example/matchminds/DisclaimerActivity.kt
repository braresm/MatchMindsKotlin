package com.example.matchminds

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import com.example.matchminds.auth.AuthActivity

class DisclaimerActivity : AppCompatActivity() {

    private lateinit var disclaimerCheckBox: CheckBox
    private lateinit var continueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disclaimer)

        disclaimerCheckBox = findViewById(R.id.disclaimerAgreeCheckBox)
        continueButton = findViewById(R.id.continueButton)

        // Set a listener on the checkbox to show / hide the "Continue" button
        disclaimerCheckBox.setOnClickListener {
            if (disclaimerCheckBox.isChecked) {
                continueButton.visibility = View.VISIBLE
            } else {
                continueButton.visibility = View.INVISIBLE
            }
        }

        // Set a click listener for the button to navigate to AuthActivity
        continueButton.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
    }
}