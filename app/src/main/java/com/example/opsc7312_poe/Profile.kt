package com.example.opsc7312_poe

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class Profile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreDatabase: FirestoreDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profileView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Initialize Firebase authentication
        auth = FirebaseAuth.getInstance()
        // Initialize the FirestoreDatabase class
        firestoreDatabase = FirestoreDatabase()

        val userId = auth.currentUser?.uid
        val name = findViewById<TextView>(R.id.profile_name)
        val email = findViewById<TextView>(R.id.profile_email)

        // Fetch user data from Firestore
        if (userId != null) {
            firestoreDatabase.getUser(userId,
                onSuccess = { document ->
                    if (document.exists()) {
                        name.text = document["name"].toString()
                        email.text = document["email"].toString()
                    }
                },
                onFailure = { exception ->
                    Toast.makeText(
                        this,
                        "Failed to load user data: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}