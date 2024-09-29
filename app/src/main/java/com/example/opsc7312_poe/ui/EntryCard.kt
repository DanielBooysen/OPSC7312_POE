package com.example.opsc7312_poe.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
//import androidx.core.net.toUri
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.BitmapFactory
import androidx.compose.ui.platform.LocalContext
import com.example.opsc7312_poe.data.FishEntry
import java.io.InputStream

@Composable
fun EntryCard(entry: FishEntry, onClick: () -> Unit) {
    // State to hold the selected image Uri
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Create a launcher to pick an image from the gallery
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // If there's an image URI, display the image
            selectedImageUri?.let { uri ->
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Fish image",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(end = 16.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            } ?: run {
                // If no image URI is available, show a placeholder or nothing
                Text(
                    text = "No Image",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp)
                )
            }

            Column {
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(entry.date),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text("Species: ${entry.species}")
                Text("Length: ${entry.length} cm")
                Text("Weight: ${entry.weight} kg")
                Button(onClick = { launcher.launch("image/*") }) {
                    Text(text = "Pick Image")
                }
            }
        }
    }
}
