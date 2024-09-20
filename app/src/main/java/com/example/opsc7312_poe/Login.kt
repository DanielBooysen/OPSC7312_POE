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


class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
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
            /* Jacques
            Read firebase to check if user has registered account
             */
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
    }
}