package com.example.opsc7312_poe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
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
                    if(document.exists()){
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

        val home = findViewById<ImageView>(R.id.home_icon)

        home.setOnClickListener{
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        val settings = findViewById<ImageView>(R.id.settings_icon)

        settings.setOnClickListener{
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        val updatePassword = findViewById<Button>(R.id.confirm_button)
        val newPassword = findViewById<TextView>(R.id.update_password)

        updatePassword.setOnClickListener{
            val userID = auth.currentUser?.uid
            val updateData = hashMapOf(
                "password" to newPassword
            )

            // Save user data to Firestore
            if (userId != null) {
                firestoreDatabase.updateUser(userId, updateData,
                    onSuccess = {
                        Toast.makeText(
                            this,
                            "Password updated!",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onFailure = { exception ->
                        Toast.makeText(
                            this,
                            "Failed to update password:${exception}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}