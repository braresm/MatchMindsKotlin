package com.example.matchminds.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matchminds.ChatActivity
import com.example.matchminds.MatchActivity
import com.example.matchminds.SwipeActivity
import com.example.matchminds.R
import com.example.matchminds.auth.LoginActivity
import com.example.matchminds.models.Account
import com.example.matchminds.repository.AuthRepository
import com.example.matchminds.repository.SkillRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {

    private var isTakingPhoto = false
    private val authRepository = AuthRepository()
    private val skillRepository = SkillRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val currentAccount: Account? = authRepository.getCurrentAccount()

        if (currentAccount == null) {
            // Redirect unauthenticated users to Login screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            return
        }

        // Change the title using title property
        title = currentAccount.name

        // Retrieve the elements from UI
        val changeProfilePictureButton = findViewById<Button>(R.id.changeProfilePictureButton)
        val ictRadioGroup = findViewById<RadioGroup>(R.id.itcRadioGroup)
        val mediaRadioButton = findViewById<RadioButton>(R.id.itcMedia)
        val softwareRadioButton = findViewById<RadioButton>(R.id.itcSoftware)
        val saveSkillsButton = findViewById<Button>(R.id.saveSkillsButton)
        val recyclerView: RecyclerView = findViewById(R.id.skillsRecyclerView)

        mediaRadioButton.tag = "media"
        softwareRadioButton.tag = "software"

        // Load the user's current profile picture
        loadProfilePicture(authRepository)

        // Get the skills for the current user and populate the recycler view
        recyclerView.layoutManager = LinearLayoutManager(this)
        skillRepository.getSkillsForAccount(currentAccount) { skills, error ->
            if (skills.isNotEmpty()) {
                val adapter = SkillAdapter(this, skills)
                recyclerView.adapter = adapter
            } else {
                Toast.makeText(applicationContext, error, Toast.LENGTH_SHORT).show()
            }
        }

        // Get the ICT skills for the current user
        skillRepository.getIctSkillsForAccount(currentAccount) { skill, error ->
            if (skill != null) {
                if (skill == "media") {
                    mediaRadioButton.isChecked = true
                }
                if (skill == "software") {
                    softwareRadioButton.isChecked = true
                }

            }
        }

        // Define listener for the "Change Profile Image" button
        changeProfilePictureButton.setOnClickListener {
            val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select an Image")
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        isTakingPhoto = true
                        checkCameraPermission()
                    }
                    1 -> {
                        isTakingPhoto = false
                        chooseFromGallery()
                    }
                    2 -> dialog.dismiss()
                }
            }
            builder.show()
        }


        // Define listener for "Save" button
        saveSkillsButton.setOnClickListener {
            val selectedRadioButtonId = ictRadioGroup.checkedRadioButtonId

            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                val selectedValue = selectedRadioButton.tag.toString()

                skillRepository.saveIctSkillForStudent(
                    currentAccount, selectedValue
                ) { success, error ->
                    if (success !== null) {
                        Toast.makeText(
                            applicationContext, success, Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext, error, Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }

        // Initialize bottom menu
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.action_profile

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_swipe -> {
                    val intent = Intent(this, SwipeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_match -> {
                    val intent = Intent(this, MatchActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_profile -> true// Already in Profile activity, do nothing
                else -> false
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            takePhoto()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now execute the action (e.g., launch the camera)
                if (isTakingPhoto) {
                    takePhoto()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Permission to access the camera was denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        pickImageLauncher.launch(takePictureIntent)
    }

    private fun chooseFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (isTakingPhoto) {
                    val imageBitmap = result.data?.extras?.get("data") as Bitmap
                    // Handle the image from camera
                    handlePhotoResult(imageBitmap)
                } else {
                    // Handle the image from gallery
                    val selectedImageUri = result.data?.data
                    if (selectedImageUri != null) {
                        uploadImageToFirebase(selectedImageUri, authRepository)
                    }
                }
            }
        }

    private fun loadProfilePicture(authRepository: AuthRepository) {
        val profileImageView = findViewById<ImageView>(R.id.profileImageView)
        authRepository.firebaseUser()?.photoUrl?.let { photoUrl ->
            if (photoUrl != null) {
                Picasso.get().load(photoUrl).into(profileImageView)
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri, authRepository: AuthRepository) {
        val user = authRepository.firebaseUser()
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference

        val filePath = storageReference.child("profile_pictures/${user?.uid}.jpg")

        filePath.putFile(imageUri).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Image uploaded successfully
                filePath.downloadUrl.addOnSuccessListener { uri ->
                    // Update the user's photoUrl
                    val profileUpdates = UserProfileChangeRequest.Builder().setPhotoUri(uri).build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Update successful
                            // Load and display the new profile picture
                            loadProfilePicture(authRepository)
                        } else {
                            // Update failed
                            // Display a toast message
                            Toast.makeText(
                                applicationContext,
                                "Error while updating profile picture",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                // Upload failed
                // Display a toast message
                Toast.makeText(
                    applicationContext, "Error while uploading profile picture", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun handlePhotoResult(imageBitmap: Bitmap) {
        val user = authRepository.firebaseUser()
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference

        val profilePictureRef = storageReference.child("profile_pictures/${user?.uid}.jpg")

        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = profilePictureRef.putBytes(data)

        uploadTask.addOnSuccessListener { _ ->
            // Get the download URL of the uploaded image
            profilePictureRef.downloadUrl.addOnSuccessListener { uri ->
                // Update the user's photoUrl
                val profileUpdates = UserProfileChangeRequest.Builder().setPhotoUri(uri).build()

                user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Update successful
                        // Load and display the new profile picture
                        loadProfilePicture(authRepository)
                    } else {
                        // Update failed
                        // Display a toast message
                        Toast.makeText(
                            applicationContext,
                            "Error while updating profile picture",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.addOnFailureListener { e ->
                // Handle the upload failure
                Toast.makeText(
                    applicationContext, "Error while uploading profile picture", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 101
    }
}