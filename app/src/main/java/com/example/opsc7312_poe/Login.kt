package com.example.opsc7312_poe

import android.R.layout
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreDatabase: FirestoreDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        //Initialize Firebase authentication
        auth = FirebaseAuth.getInstance()
        // Initialize the FirestoreDatabase class
        firestoreDatabase = FirestoreDatabase()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get the reference to the TextView
        val signUpLink = findViewById<TextView>(R.id.signUpLink)

        // Set OnClickListener to the TextView
        signUpLink.setOnClickListener {
            // Start RegisterActivity
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        val signIn = findViewById<Button>(R.id.signInButton)

        signIn.setOnClickListener {
            // Read firebase to check if user has registered account

            val email = findViewById<TextView>(R.id.emailEditText).text.toString()
            val password = findViewById<TextView>(R.id.passwordEditText).text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid

                            // Fetch user data from Firestore
                            if (userId != null) {
                                firestoreDatabase.getUser(userId,
                                    onSuccess = { document ->
                                        // You can now use the user data
                                        val userData = document.data
                                        val intent = Intent(this, Home::class.java)
                                        startActivity(intent)
                                        finish()
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
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }

        }
    }
}