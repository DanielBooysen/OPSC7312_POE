package com.example.opsc7312_poe

import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun fetchFishEntries() {
        // Check for network connectivity
        if (isNetworkAvailable(this)) {
            // Fetch data from Firebase Firestore
            fetchFromFirestore()
        } else {
            // Fetch data from SQLite if offline
            fetchFromLocalDatabase()
        }
    }

    private fun fetchFromLocalDatabase() {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase
        val userId = auth.currentUser?.uid // Get the current user's ID
        if (userId == null) {
            Log.e(TAG, "User not authenticated")
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        // Query SQLite database, filtering by userId
        val cursor = db.query(
            "FishEntries",
            arrayOf("fishSpecies", "length", "weight", "baitUsed", "timeOfDay", "time", "weather", "location"),
            "userId = ?", // Filter by userId
            arrayOf(userId),
            null, null, null
        )

        val fishEntriesList = mutableListOf<FishEntry>()
        while (cursor.moveToNext()) {
            val fishEntry = FishEntry(
                species = cursor.getString(cursor.getColumnIndexOrThrow("fishSpecies")),
                length = cursor.getString(cursor.getColumnIndexOrThrow("length")),
                weight = cursor.getString(cursor.getColumnIndexOrThrow("weight")),
                baitUsed = cursor.getString(cursor.getColumnIndexOrThrow("baitUsed")),
                timeOfDay = cursor.getString(cursor.getColumnIndexOrThrow("timeOfDay")),
                time = cursor.getString(cursor.getColumnIndexOrThrow("time")),
                weather = cursor.getString(cursor.getColumnIndexOrThrow("weather")),
                location = cursor.getString(cursor.getColumnIndexOrThrow("location"))
            )
            fishEntriesList.add(fishEntry)
        }
        cursor.close()

        if (fishEntriesList.isNotEmpty()) {
            adapter.setFishEntries(fishEntriesList)
            Log.d(TAG, "Fish entries loaded from local database")
        } else {
            Log.d(TAG, "No fish entries found in the local database")
            Toast.makeText(this, "No offline fish logs found", Toast.LENGTH_SHORT).show()
        }
    }


    private fun fetchFromFirestore() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            Log.d(TAG, "Fetching fish entries for user: $userId")
            firestore.collection("fishEntries")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
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
                        adapter.setFishEntries(fishEntriesList)
                        Log.d(TAG, "Fish entries loaded successfully")
                    } else {
                        Log.d(TAG, "No fish entries found for this user")
                        Toast.makeText(this, "No fish logs found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error loading fish entries: ${exception.message}")
                    Toast.makeText(this, "Error loading logs: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.e(TAG, "User not authenticated")
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}
// This code has a On create which initializes Firebase, which at the end fetches the users logged data from Firestore
// The following code was taken from Stack Overflow
// Author: Stack Overflow
// Link:https://stackoverflow.com/questions/28929637/difference-and-uses-of-oncreate-oncreateview-and-onactivitycreated-in-fra#:~:text=The%20onCreate()%20method%20in%20a%20Fragment