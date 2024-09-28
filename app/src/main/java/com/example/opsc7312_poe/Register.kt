package com.example.opsc7312_poe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreDatabase: FirestoreDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        //Initialize Firebase authentication
        auth = FirebaseAuth.getInstance()
        // Initialize the FirestoreDatabase class
        firestoreDatabase = FirestoreDatabase()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Get the reference to the TextView
        val signUpLink = findViewById<TextView>(R.id.signInLink)

        // Set OnClickListener to the TextView
        signUpLink.setOnClickListener {
            // Start RegisterActivity
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        val registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            //Save user data to new account in database
            val email = findViewById<TextView>(R.id.emailEditText).text.toString()
            val password = findViewById<TextView>(R.id.passwordEditText).text.toString()
            val name = findViewById<TextView>(R.id.nameEditText).text.toString()


            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid

                            // Prepare user data map
                            val userData = hashMapOf(
                                "email" to email,
                                "password" to password,
                                "name" to name
                            )

                            // Save user data to Firestore
                            if (userId != null) {
                                firestoreDatabase.addUser(userId, userData,
                                    onSuccess = {
                                        val intent = Intent(this, Home::class.java)
                                        startActivity(intent)
                                        finish()
                                    },
                                    onFailure = { exception ->
                                        Toast.makeText(
                                            this,
                                            "Failed to save user data: ${exception.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }

                        } else {
                            Toast.makeText(
                                baseContext,
                                "Registration failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    this,
                    "Please enter email and password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}