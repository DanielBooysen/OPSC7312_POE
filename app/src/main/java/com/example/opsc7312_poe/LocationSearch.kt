package com.example.opsc7312_poe

import android.os.Bundle
import android.widget.*
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class LocationSearch : AppCompatActivity() {

    private lateinit var locationSpinner: Spinner
    private lateinit var okButton: Button
    private lateinit var backButton: Button
    private lateinit var resultsTextView: TextView
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_search)

        locationSpinner = findViewById(R.id.locationSpinner)
        okButton = findViewById(R.id.okButton)  // Initialize OK button
        backButton = findViewById(R.id.backButton)  // Initialize Back button
        resultsTextView = findViewById(R.id.resultsTextView)

        // Set up the Spinner with locations array
        ArrayAdapter.createFromResource(
            this,
            R.array.dam_locations,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            locationSpinner.adapter = adapter
        }

        // Spinner item selected listener
        locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val location = parent.getItemAtPosition(position).toString()
                searchCatchesByLocation(location)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                resultsTextView.text = "Please select a location"
            }
        }

        // OK button click listener to clear the results view
        okButton.setOnClickListener {
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

                        // Append all the relevant details to results
                        results.append("Species: $species\n")
                            .append("Length: $length cm\n")
                            .append("Weight: $weight kg\n")
                            .append("Bait Used: $baitUsed\n")
                            .append("Time of Day: $timeOfDay\n")
                            .append("Time: $time\n")
                            .append("Weather: $weather\n")
                            .append("Location: $location\n\n")
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
