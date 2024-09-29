package com.example.opsc7312_poe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.opsc7312_poe.data.FishEntry


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FishJournalApp() {
    val navController = rememberNavController()
    var entries by remember { mutableStateOf(listOf<FishEntry>()) }
    var showDialog by remember { mutableStateOf(false) }

    val topAppBarColor = Color(0xFFEB7330) // This is the hex color #eb7330

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Fish Journal", color = Color.White) },
                        actions = {
                            IconButton(onClick = { showDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Add Entry", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = topAppBarColor
                        )
                    )
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    items(entries.sortedByDescending { it.date }) { entry ->
                        EntryCard(entry) {
                            navController.navigate("entry/${entry.id}")
                        }
                    }
                }
            }

            if (showDialog) {
                AddEntryDialog(
                    onDismiss = { showDialog = false },
                    onAddEntry = { newEntry ->
                        entries = entries + newEntry
                        showDialog = false
                    }
                )
            }
        }
        composable("entry/{entryId}") { backStackEntry ->
            val entryId = backStackEntry.arguments?.getString("entryId")
            val entry = entries.find { it.id.toString() == entryId }
            if (entry != null) {
                EntryDetailScreen(entry = entry, onBack = { navController.popBackStack() })
            }
        }
    }
}