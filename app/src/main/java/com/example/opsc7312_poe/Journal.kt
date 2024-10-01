package com.example.opsc7312_poe

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

//import com.google.firebase.auth.FirebaseAuth

class Journal : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: FishEntryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_journal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fishingJournal)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

            // Initialize Firebase authentication and Firestore
            auth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()
        adapter = FishEntryAdapter(mutableListOf<FishEntry>())

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewFishEntries)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val firestoreDatabase = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()


fetchFishEntries()

        }

    private fun fetchFishEntries() {
        // Get the current user's ID
        val userId = auth.currentUser?.uid
        // Check if the user is authenticated
        if (userId != null) {
            // Log the query attempt for debugging
            Log.d(TAG, "Fetching fish entries for user: $userId")
            // Query Firestore for fish entries with the matching userId
            firestore.collection("fishEntries")
                .whereEqualTo("userId", userId)  // Only fetch entries where userId matches the current user
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        // Map each document to a FishEntry object
                        val fishEntriesList = querySnapshot.documents.map { document ->
                            val log = document.data ?: emptyMap<String, Any>()
                            FishEntry(
                                species = log["species"] as? String ?: "",
                                length = log["length"] as? String ?: "",
                                weight = log["weight"] as? String ?: "",
                                baitUsed = log["baitUsed"] as? String ?: "",
                                timeOfDay = log["timeOfDay"] as? String ?: "",
                                time = log["time"] as? String ?: "",
                                weather = log["weather"] as? String ?: "",
                                location = log["location"] as? String ?: ""
                            )
                        }.toMutableList()
                        // Update the adapter with the fetched entries
                        adapter.setFishEntries(fishEntriesList)
                        Log.d(TAG, "Fish entries loaded successfully")
                    } else {
                        // No entries found for the user
                        Log.d(TAG, "No fish entries found for this user")
                        Toast.makeText(this, "No fish logs found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    // Log and notify the user if there's an error
                    Log.e(TAG, "Error loading fish entries: ${exception.message}")
                    Toast.makeText(this, "Error loading logs: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // User is not authenticated
            Log.e(TAG, "User not authenticated")
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }




}
// This code has a On create which initializes Firebase, which at the end fetches the users logged data from Firestore
// The following code was taken from Stack Overflow
// Author: Stack Overflow
// Link:https://stackoverflow.com/questions/28929637/difference-and-uses-of-oncreate-oncreateview-and-onactivitycreated-in-fra#:~:text=The%20onCreate()%20method%20in%20a%20Fragment