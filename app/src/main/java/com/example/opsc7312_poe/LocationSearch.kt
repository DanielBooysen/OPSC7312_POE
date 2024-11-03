package com.example.opsc7312_poe

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class LocationSearch : AppCompatActivity() {

    private lateinit var locationSearchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var okButton: Button
    private lateinit var backButton: Button
    private lateinit var resultsTextView: TextView
    private val validDams = listOf("Roodeplaat Dam", "Rietvlei Dam", "Hartebeespoort Dam", "Bronkhorstspruit Dam", "Vaal Dam")
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_search)

        locationSearchEditText = findViewById(R.id.locationSearchEditText)
        searchButton = findViewById(R.id.searchButton)
        okButton = findViewById(R.id.okButton)  // Initialize OK button
        backButton = findViewById(R.id.backButton)  // Initialize Back button
        resultsTextView = findViewById(R.id.resultsTextView)

        // Search button click listener
        searchButton.setOnClickListener {
            val location = locationSearchEditText.text.toString().trim()
            if (location in validDams) {
                searchCatchesByLocation(location)
            } else {
                resultsTextView.text = "Unknown location. Please enter a valid dam name."
            }
        }

        // OK button click listener to clear the search field
        okButton.setOnClickListener {
            locationSearchEditText.text.clear() // Clear the search field
            resultsTextView.text = "Results will appear here" // Reset results message
            Toast.makeText(this, "Search cleared", Toast.LENGTH_SHORT).show() // Optional feedback
        }

        // Back button click listener
        backButton.setOnClickListener {
            finish() // Close the activity
        }
    }

    private fun searchCatchesByLocation(location: String) {
        firestore.collection("fishEntries")
            .whereEqualTo("location", location)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val results = StringBuilder()
                    for (document in querySnapshot.documents) {
                        val species = document.getString("species") ?: "Unknown Species"
                        val length = document.getString("length") ?: "Unknown Length"
                        val weight = document.getString("weight") ?: "Unknown Weight"
                        val baitUsed = document.getString("baitUsed") ?: "Unknown Bait"
                        val timeOfDay = document.getString("timeOfDay") ?: "Unknown Time of Day"
                        val time = document.getString("time") ?: "Unknown Time"
                        val weather = document.getString("weather") ?: "Unknown Weather"

                        // Append all the relevant details Rietto results
                        results.append("Species: $species\n")
                            .append("Length: $length cm\n")
                            .append("Weight: $weight kg\n")
                            .append("Bait Used: $baitUsed\n")
                            .append("Time of Day: $timeOfDay\n")
                            .append("Time: $time\n")
                            .append("Weather: $weather\n")
                            .append("Location: $location\n\n") // Additional line break for readability
                    }
                    resultsTextView.text = results.toString()
                } else {
                    resultsTextView.text = "No catches found for $location."
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load catches", Toast.LENGTH_SHORT).show()
            }
    }
}
