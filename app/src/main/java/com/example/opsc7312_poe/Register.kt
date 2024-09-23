package com.example.opsc7312_poe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
//import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {
   // private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
       //auth = FirebaseAuth.getInstance()
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

        registerButton.setOnClickListener{
           //Save user data to new account in database
            /*
 val email = findViewById<TextView>(R.id.emailEditText).text.toString()
            val password = findViewById<TextView>(R.id.passwordEditText).text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
             */
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            //finish()
            /*
            } else {
                Toast.makeText(baseContext, "Registration failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
             */
        }
    }
}