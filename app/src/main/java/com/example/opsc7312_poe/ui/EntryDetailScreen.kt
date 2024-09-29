package com.example.opsc7312_poe.ui

import android.net.Uri
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.opsc7312_poe.data.FishEntry
import java.text.SimpleDateFormat
import java.util.*
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailScreen(entry: FishEntry, onBack: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Entry Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            entry.imageUri?.let { uriString ->
                val uri = Uri.parse(uriString)
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Display the image if the bitmap was successfully loaded
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Fish image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(entry.date),
                style = MaterialTheme.typography.headlineSmall
            )
            Text("Species: ${entry.species}")
            Text("Length: ${entry.length} cm")
            Text("Weight: ${entry.weight} kg")
            Text("Bait: ${entry.bait}")
            Text("Time of Day: ${entry.timeOfDay}")
            Text("Time: ${entry.time}")
            Text("Weather: ${entry.weather}")
            Text("Location: ${entry.location}")
        }
    }
}
