package com.example.opsc7312_poe

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fishingJournal = findViewById<ImageView>(R.id.fishingJournal)

        fishingJournal.setOnClickListener {
            val intent = Intent(this, FishJournalActivity::class.java)
            startActivity(intent)
        }

        val logAFish = findViewById<ImageView>(R.id.logAFish)

        logAFish.setOnClickListener {
            val intent = Intent(this, FishLogging::class.java)
            startActivity(intent)
        }

        val locationWeather = findViewById<ImageView>(R.id.locationWeather)

        locationWeather.setOnClickListener {
            val intent = Intent(this, LocationWeather::class.java)
            startActivity(intent)
        }

        val communityNewsFeed = findViewById<ImageView>(R.id.communityNewsFeed)

        communityNewsFeed.setOnClickListener {
            val intent = Intent(this, CommunityNewsFeed::class.java)
            startActivity(intent)
        }

        val profile = findViewById<ImageView>(R.id.profile)

        profile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        val settings = findViewById<ImageView>(R.id.settings)

        settings.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }
    }
}