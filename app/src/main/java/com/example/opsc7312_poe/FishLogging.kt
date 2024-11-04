package com.example.opsc7312_poe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class FishLogging : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var spinnerFishSpecies: Spinner
    private lateinit var inputLength: EditText
    private lateinit var inputWeight: EditText
    private lateinit var inputBaitUsed: EditText
    private lateinit var spinnerTimeOfDay: Spinner
    private lateinit var inputTime: EditText
    private lateinit var spinnerWeather: Spinner
    private lateinit var spinnerLocation: Spinner
    private lateinit var buttonAddEntry: Button
    private lateinit var buttonCancelEntry: Button
    private lateinit var buttonSelectImage: Button
    private lateinit var imageViewFish: ImageView

    private var imageUri: Uri? = null // Make imageUri nullable to handle optional image selection
    private val storageReference = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fish_logging)

        // Initialize Firebase authentication and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        spinnerFishSpecies = findViewById(R.id.spinnerFishSpecies)
        inputLength = findViewById(R.id.inputLength)
        inputWeight = findViewById(R.id.inputWeight)
        inputBaitUsed = findViewById(R.id.inputBaitUsed)
        spinnerTimeOfDay = findViewById(R.id.spinnerTimeOfDay)
        inputTime = findViewById(R.id.inputTime)
        spinnerWeather = findViewById(R.id.spinnerWeather)
        spinnerLocation = findViewById(R.id.spinnerLocation)
        buttonAddEntry = findViewById(R.id.buttonAddEntry)
        buttonCancelEntry = findViewById(R.id.buttonCancelEntry)
        buttonSelectImage = findViewById(R.id.buttonSelectImage)
        imageViewFish = findViewById(R.id.imageViewFish)

        // Button click listeners
        buttonSelectImage.setOnClickListener {
            openGallery() // Open the gallery when the button is clicked
        }

        buttonAddEntry.setOnClickListener {
            val fishSpecies = spinnerFishSpecies.selectedItem.toString()
            val length = inputLength.text.toString()
            val weight = inputWeight.text.toString()
            val baitUsed = inputBaitUsed.text.toString()
            val timeOfDay = spinnerTimeOfDay.selectedItem.toString()
            val time = inputTime.text.toString()
            val weather = spinnerWeather.selectedItem.toString()
            val location = spinnerLocation.selectedItem.toString()

            if (fishSpecies.isEmpty() || length.isEmpty() || weight.isEmpty() || baitUsed.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user != null) {
                // Create FishEntry map
                val fishEntry = hashMapOf<String, Any>(
                    "species" to fishSpecies,
                    "length" to length,
                    "weight" to weight,
                    "baitUsed" to baitUsed,
                    "timeOfDay" to timeOfDay,
                    "time" to time,
                    "weather" to weather,
                    "location" to location,
                    "userId" to user.uid
                )

                // Check if an image URI is present
                if (imageUri != null) {
                    // If image is selected, upload it and add URL to entry
                    uploadImageToFirebase(imageUri!!) { imageUrl ->
                        fishEntry["imageUrl"] = imageUrl
                        addEntryToFirestore(fishEntry)
                    }
                } else {
                    // If no image is selected, directly add entry without image URL
                    addEntryToFirestore(fishEntry)
                }
            } else {
                Toast.makeText(this, "Please log in to add entries.", Toast.LENGTH_SHORT).show()
            }
        }

        buttonCancelEntry.setOnClickListener {
            finish()
        }
    }

    // Open gallery to select image
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager) != null) {
            getImage.launch(intent)
        } else {
            Toast.makeText(this, "No gallery app found", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle selected image result
    private val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            imageUri = result.data!!.data!!
            imageViewFish.setImageURI(imageUri)
        } else {
            Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show()
        }
    }

    // Upload image to Firebase Storage
    private fun uploadImageToFirebase(uri: Uri, onSuccess: (String) -> Unit) {
        val uniqueID = UUID.randomUUID().toString()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"
        val imageRef = storageReference.child("images/$userId/$uniqueID.jpg")

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to add entry to Firestore
    private fun addEntryToFirestore(fishEntry: HashMap<String, Any>) {
        firestore.collection("fishEntries")
            .add(fishEntry)
            .addOnSuccessListener {
                Toast.makeText(this, "Log added, Check Journal", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to add log: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}


// This code contains a fish logging activity, which handlex user input, for fish logging entries saving to Firebase.
// The following code was taken from Android
// Author: Android
// Link: https://developer.android.com/guide/components/activities/activity-lifecycle#:~:text=An%20Activity%20is%20an%20application%20component
