package com.example.opsc7312_poe

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SpeciesSearch : AppCompatActivity() {

    private lateinit var speciesSpinner: Spinner
    private lateinit var searchButton: Button
    private lateinit var resultsTextView: TextView
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_species_search)

        speciesSpinner = findViewById(R.id.speciesSpinner)
        searchButton = findViewById(R.id.searchButton)
        resultsTextView = findViewById(R.id.resultsTextView)

        // Load species list from resources
        val speciesList = resources.getStringArray(R.array.fish_species)

        // Set up ArrayAdapter to populate the Spinner with speciesList
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, speciesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        speciesSpinner.adapter = adapter

        // Set up search button click listener
        searchButton.setOnClickListener {
            // Get selected species from the dropdown menu
            val selectedSpecies = speciesSpinner.selectedItem.toString()
            searchCatchesBySpecies(selectedSpecies)
        }
    }

    private fun searchCatchesBySpecies(species: String) {
        firestore.collection("fishEntries")
            .whereEqualTo("species", species)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val results = StringBuilder()
                    for (document in querySnapshot.documents) {
                        val length = document.getString("length") ?: "Unknown Length"
                        val weight = document.getString("weight") ?: "Unknown Weight"
                        val baitUsed = document.getString("baitUsed") ?: "Unknown Bait"
                        val timeOfDay = document.getString("timeOfDay") ?: "Unknown Time of Day"
                        val location = document.getString("location") ?: "Unknown Location"

                        results.append("Species: $species\n")
                            .append("Length: $length cm\n")
                            .append("Weight: $weight kg\n")
                            .append("Bait Used: $baitUsed\n")
                            .append("Time of Day: $timeOfDay\n")
                            .append("Location: $location\n\n")
                    }
                    resultsTextView.text = results.toString()
                } else {
                    resultsTextView.text = "No catches found for species: $species."
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load catches", Toast.LENGTH_SHORT).show()
            }
    }
}