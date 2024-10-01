package com.example.opsc7312_poe

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

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreDatabase: FirestoreDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Initialize Firebase authentication
        auth = FirebaseAuth.getInstance()
        firestoreDatabase = FirestoreDatabase()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Sign-up link listener
        val signUpLink = findViewById<TextView>(R.id.signUpLink)
        signUpLink.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        val signIn = findViewById<Button>(R.id.signInButton)
        signIn.setOnClickListener {
            val email = findViewById<TextView>(R.id.emailEditText).text.toString()
            val password = findViewById<TextView>(R.id.passwordEditText).text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                firestoreDatabase.getUser(userId,
                                    onSuccess = { document ->
                                        if (document.exists()) {
                                            if (document["email"] == email && document["password"] == password) {
                                                val intent = Intent(this, Home::class.java)
                                                startActivity(intent)
                                                finish()
                                            }
                                        }
                                    },
                                    onFailure = { exception ->
                                        Toast.makeText(this, "Failed to load user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        } else {
                            Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}