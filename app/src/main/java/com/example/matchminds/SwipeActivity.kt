package com.example.matchminds

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import com.example.matchminds.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.os.Vibrator


class SwipeActivity : AppCompatActivity(), MotionLayout.TransitionListener {
    private lateinit var motionLayout: MotionLayout
    private val imageResources = listOf(
        R.drawable.kevin, R.drawable.john, R.drawable.jacky, R.drawable.mathew
    )

    private var currentImageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe)

        // Change the title using title property
        title = "Swipe"

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.action_swipe

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_swipe -> true // Already in Swipe activity, do nothing
                R.id.action_match -> {
                    val intent = Intent(this, MatchActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        motionLayout = findViewById(R.id.frameLayout2)
        motionLayout.setTransitionListener(this)


    }

    override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {

    }

    override fun onTransitionChange(
        motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float
    ) {

    }

    override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
        if (currentId == R.id.offScreenUnlike) {
            // Transition startToUnlike is completed
            // Do something here
            motionLayout?.progress = 0f
            motionLayout?.setTransition(R.id.start, R.id.unlike)
            val imageView = motionLayout?.findViewById<ImageView>(R.id.imageView)
            //imageView?.setImageResource(R.drawable.kevin)
            if (currentImageIndex >= 3) {
                currentImageIndex = 0
            } else {
                currentImageIndex++
            }
        } else if (currentId == R.id.offScreenLike) {
            // Transition startToLike is completed
            // Do something here
            motionLayout?.progress = 0f
            motionLayout?.setTransition(R.id.start, R.id.like)
            val imageView = motionLayout?.findViewById<ImageView>(R.id.imageView)
            //imageView?.setImageResource(R.drawable.kevin)
            if (currentImageIndex >= 3) {
                currentImageIndex = 0
            } else {
                currentImageIndex++
            }
        }
        // Change the image for the ImageView on each click

        updateImageForImageView(motionLayout)

        Log.d("TinderFragment", "Current Image Index: $currentImageIndex")

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Vibrate for 500 milliseconds (0.5 seconds)
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(500)
        }
    }

    private fun updateImageForImageView(view: View?) {
        val imageView = view?.findViewById<ImageView>(R.id.imageView)
        imageView?.setImageResource(imageResources[currentImageIndex])
    }


    override fun onTransitionTrigger(
        motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float
    ) {

    }
}