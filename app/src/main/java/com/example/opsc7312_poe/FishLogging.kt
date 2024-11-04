package com.example.opsc7312_poe

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
    private lateinit var imageViewFish: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
        imageViewFish = findViewById(R.id.imageViewFish)

        // Button click listeners
        buttonAddEntry.setOnClickListener {
            // Logic to handle adding the fish entry
            val fishSpecies = spinnerFishSpecies.selectedItem.toString()
            val length = inputLength.text.toString()
            val weight = inputWeight.text.toString()
            val baitUsed = inputBaitUsed.text.toString()
            val timeOfDay = spinnerTimeOfDay.selectedItem.toString()
            val time = inputTime.text.toString()
            val weather = spinnerWeather.selectedItem.toString()
            val location = spinnerLocation.selectedItem.toString()

            // Simple validation
            if (fishSpecies.isEmpty() || length.isEmpty() || weight.isEmpty() || baitUsed.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the user is logged in before adding the entry
            val user = auth.currentUser
            if (user != null) {
                // Prepare the fish entry data
                val fishEntry = hashMapOf(
                    "species" to fishSpecies,
                    "length" to length,
                    "weight" to weight,
                    "baitUsed" to baitUsed,
                    "timeOfDay" to timeOfDay,
                    "time" to time,
                    "weather" to weather,
                    "location" to location,
                    "userId" to user.uid // Associate the entry with the user
                )

                // Save the entry to Firestore and local database
                firestore.collection("fishEntries")
                    .add(fishEntry)
                    .addOnSuccessListener {
                        val dbHelper = DatabaseHelper(this)
                        val success = dbHelper.insertFishEntry(
                            userId = user.uid,
                            fishSpecies = spinnerFishSpecies.selectedItem.toString(),
                            length = inputLength.text.toString(),
                            weight = inputWeight.text.toString(),
                            baitUsed = inputBaitUsed.text.toString(),
                            timeOfDay = spinnerTimeOfDay.selectedItem.toString(),
                            time = inputTime.text.toString(),
                            weather = spinnerWeather.selectedItem.toString(),
                            location = spinnerLocation.selectedItem.toString()
                        )

                        if (success) {
                            Toast.makeText(this, "Entry saved offline", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to save entry", Toast.LENGTH_SHORT).show()
                        }

                        Toast.makeText(this, "Log added, Check Journal", Toast.LENGTH_SHORT).show()
                        finish() // Close the activity or clear fields if needed
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Failed to add log: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please log in to add entries.", Toast.LENGTH_SHORT).show()
            }
        }

        buttonCancelEntry.setOnClickListener {
            // Logic to handle canceling the entry
            finish() // Simply close the activity

                    }
                }
            }

// This code contains a fish logging activity, which handlex user input, for fish logging entries saving to Firebase.
// The following code was taken from Android
// Author: Android
// Link: https://developer.android.com/guide/components/activities/activity-lifecycle#:~:text=An%20Activity%20is%20an%20application%20component
