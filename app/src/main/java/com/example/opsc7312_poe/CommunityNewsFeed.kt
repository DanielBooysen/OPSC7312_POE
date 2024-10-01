package com.example.opsc7312_poe

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//import com.google.firebase.auth.FirebaseAuth

class CommunityNewsFeed : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: FishEntryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_news_feed)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.newsFeed)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase authentication
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        adapter = FishEntryAdapter(mutableListOf<FishEntry>())
        val recyclerView = findViewById<RecyclerView>(R.id.recViewComFeed)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchFishEntries()
    }

    private fun fetchFishEntries() {
        // Get the current user's ID
        val userId = auth.currentUser?.uid
        // Check if the user is authenticated
        if (userId != null) {
            // Log the query attempt for debugging
            Log.d(ContentValues.TAG, "Fetching fish entries")
            // Query Firestore for fish entries
            firestore.collection("fishEntries")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        // Map each document to a FishEntry object, assuming each entry has a userId field
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
                                location = log["location"] as? String ?: "",
                                userId = log["userId"] as? String ?: ""
                            )
                        }.toMutableList()

                        // For each fish entry, load the corresponding user data based on userId
                        fishEntriesList.forEach { fishEntry ->
                            val userId = fishEntry.userId
                            if (userId.isNotEmpty()) {
                                // Fetch user data from Firestore based on userId
                                firestore.collection("users")
                                    .document(userId)
                                    .get()
                                    .addOnSuccessListener { userDocument ->
                                        if (userDocument.exists()) {
                                            val userName =
                                                userDocument.getString("name") ?: "Unknown User"
                                            val userEmail =
                                                userDocument.getString("email") ?: "Unknown Email"

                                            fishEntry.userName = userName
                                            fishEntry.userEmail = userEmail

                                            adapter.setFishEntries(fishEntriesList)
                                        } else {
                                            Log.d(
                                                ContentValues.TAG,
                                                "User not found for userId: $userId"
                                            )
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e(
                                            ContentValues.TAG,
                                            "Error loading user data: ${exception.message}"
                                        )
                                    }
                            }
                        }
                    } else {
                        Log.d(ContentValues.TAG, "No fish entries found")
                        Toast.makeText(this, "No fish logs found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(ContentValues.TAG, "Error loading fish entries: ${exception.message}")
                    Toast.makeText(
                        this,
                        "Error loading logs: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}