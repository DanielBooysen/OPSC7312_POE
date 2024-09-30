package com.example.opsc7312_poe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.opsc7312_poe.ui.FishJournalApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Fish Journal Activity - for Firebase Authentication and Fire store
class  FishJournalApp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreDatabase: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_journal)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Initialize Fire store
        firestoreDatabase = FirebaseFirestore.getInstance()
    }
}

// FishJournalActivity - for Jetpack Compose UI
class FishJournalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FishJournalApp() // Custom Compose App UI
                }
            }
        }
    }
}

