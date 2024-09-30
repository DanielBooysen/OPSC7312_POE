package com.example.opsc7312_poe.ui

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.opsc7312_poe.data.FishEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryDialog(onDismiss: () -> Unit, onAddEntry: (Any?) -> Unit) {
    // State variables for entry fields
    var species by remember { mutableStateOf("") }
    var length by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var bait by remember { mutableStateOf("") }
    var timeOfDay by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var weather by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Firebase instances
    val db = FirebaseFirestore.getInstance()  // Firestore instance
    val storage = FirebaseStorage.getInstance().reference  // Firebase Storage reference

    // Expanded states for dropdowns
    var speciesExpanded by remember { mutableStateOf(false) }
    var timeOfDayExpanded by remember { mutableStateOf(false) }
    var weatherExpanded by remember { mutableStateOf(false) }
    var locationExpanded by remember { mutableStateOf(false) }

    // Dropdown options
    val speciesOptions = listOf("Common Carp", "Grass Carp", "Blue Kurper", "Redbreast Kurper", "Canary Kurper", "Largemouth Bass", "Smallmouth Bass", "African Sharptooth Catfish", "Largemouth Yellowfish", "Smallmouth Yellowfish", "Mudfish", "Rainbow Trout", "Brown Trout")
    val locationOptions = listOf("Roodeplaat dam", "Rietvlei dam", "Hartebeespoort dam", "Bronkhorspruit dam", "Vaal dam")
    val timeOfDayOptions = listOf("early morning", "late morning", "early afternoon", "late afternoon", "early evening", "late evening")
    val weatherOptions = listOf("sunny", "rainy", "windy", "stormy", "snowy", "cloudy")

    // Image picker launcher
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    // Function to upload image and return the download URL (run outside composable scope)
    suspend fun uploadImage(uri: Uri): String? {
        return try {
            val ref = storage.child("images/${UUID.randomUUID()}.jpg")
            ref.putFile(uri).await()  // Upload image to Firebase Storage
            ref.downloadUrl.await().toString()  // Get the download URL
        } catch (e: Exception) {
            Log.e("Firebase", "Image upload failed: $e")
            null
        }
    }

    // Function to save the fish entry to Firestore (run outside composable scope)
    fun saveFishEntry(fishEntry: FishEntry) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("fishEntries").add(fishEntry).await()
                Log.d("Firebase", "Fish entry added successfully")
            } catch (e: Exception) {
                Log.e("Firebase", "Error adding fish entry: $e")
            }
        }
    }

    // Dialog to add new fish entry
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Entry") },
        text = {
            Column(modifier = Modifier.padding(16.dp)) {
                // Dropdown for species
                ExposedDropdownMenuBox(
                    expanded = speciesExpanded,
                    onExpandedChange = { speciesExpanded = !speciesExpanded }
                ) {
                    OutlinedTextField(
                        value = species,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Species") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = speciesExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = speciesExpanded,
                        onDismissRequest = { speciesExpanded = false }
                    ) {
                        speciesOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    species = option
                                    speciesExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = length,
                    onValueChange = { length = it },
                    label = { Text("Length (cm)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = bait,
                    onValueChange = { bait = it },
                    label = { Text("Bait Used") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Dropdown for time of day
                ExposedDropdownMenuBox(
                    expanded = timeOfDayExpanded,
                    onExpandedChange = { timeOfDayExpanded = !timeOfDayExpanded }
                ) {
                    OutlinedTextField(
                        value = timeOfDay,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Time of Day") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeOfDayExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = timeOfDayExpanded,
                        onDismissRequest = { timeOfDayExpanded = false }
                    ) {
                        timeOfDayOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    timeOfDay = option
                                    timeOfDayExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Dropdown for weather
                ExposedDropdownMenuBox(
                    expanded = weatherExpanded,
                    onExpandedChange = { weatherExpanded = !weatherExpanded }
                ) {
                    OutlinedTextField(
                        value = weather,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Weather") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = weatherExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = weatherExpanded,
                        onDismissRequest = { weatherExpanded = false }
                    ) {
                        weatherOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    weather = option
                                    weatherExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Dropdown for location
                ExposedDropdownMenuBox(
                    expanded = locationExpanded,
                    onExpandedChange = { locationExpanded = !locationExpanded }
                ) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Location") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = locationExpanded,
                        onDismissRequest = { locationExpanded = false }
                    ) {
                        locationOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    location = option
                                    locationExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Button to trigger image selection
                Button(onClick = { imagePicker.launch("image/*") }) {
                    Text("Select Image")
                }

                // Display selected image URI (if any)
                imageUri?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Image selected: ${it.lastPathSegment}")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                // Coroutine to handle Firebase operations
                CoroutineScope(Dispatchers.Main).launch {
                    val imageUrl = imageUri?.let { uploadImage(it) }
                    val fishEntry = FishEntry(
                        date = Date(),
                        species = species,
                        length = length.toFloatOrNull() ?: 0f,
                        weight = weight.toFloatOrNull() ?: 0f,
                        bait = bait,
                        timeOfDay = timeOfDay,
                        time = time,
                        weather = weather,
                        location = location,
                        imageUri = imageUrl  // Store the image URL from Firebase Storage
                    )

                    // Save fish entry to Firestore
                    saveFishEntry(fishEntry)

                    onAddEntry()  // Callback after entry is added
                    onDismiss()
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun onAddEntry() {
    TODO("Not yet implemented")
}
