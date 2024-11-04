package com.example.opsc7312_poe

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "OfflineDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create the FishEntries table
        val createTable = """
            CREATE TABLE FishEntries (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId TEXT,
                fishSpecies TEXT,
                length TEXT,
                weight TEXT,
                baitUsed TEXT,
                timeOfDay TEXT,
                time TEXT,
                weather TEXT,
                location TEXT
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS FishEntries")
        onCreate(db)
    }

    // Method to insert a fish entry
    fun insertFishEntry(
        userId: String,
        fishSpecies: String,
        length: String,
        weight: String,
        baitUsed: String,
        timeOfDay: String,
        time: String,
        weather: String,
        location: String
    ): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("userId", userId)
            put("fishSpecies", fishSpecies)
            put("length", length)
            put("weight", weight)
            put("baitUsed", baitUsed)
            put("timeOfDay", timeOfDay)
            put("time", time)
            put("weather", weather)
            put("location", location)
        }
        val result = db.insert("FishEntries", null, contentValues)
        return result != -1L
    }
}

