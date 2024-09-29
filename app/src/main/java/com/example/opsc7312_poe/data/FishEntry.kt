package com.example.opsc7312_poe.data

import java.util.*


data class FishEntry(
    val id: UUID = UUID.randomUUID(),
    val date: Date,
    val species: String,
    val length: Float,
    val weight: Float,
    val bait: String,
    val timeOfDay: String,
    val time: String,
    val weather: String,
    val location: String,
    val imageUri: String? = null
)