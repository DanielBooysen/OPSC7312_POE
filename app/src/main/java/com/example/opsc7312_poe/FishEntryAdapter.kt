package com.example.opsc7312_poe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QuerySnapshot

abstract class FishEntryAdapter(private var fishEntries: List<FishEntry>) : RecyclerView.Adapter<FishEntryAdapter.FishEntryViewHolder>() {

    class FishEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewSpecies: TextView = itemView.findViewById(R.id.textViewSpecies)
        val textViewLength: TextView = itemView.findViewById(R.id.textViewLength)
        val textViewWeight: TextView = itemView.findViewById(R.id.textViewWeight)
        // Add more views as needed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishEntryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_fish_entry, parent, false)
        return FishEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: FishEntryViewHolder, position: Int) {
        val currentEntry = fishEntries[position]
        holder.textViewSpecies.text = currentEntry.species
        holder.textViewLength.text = "Length: ${currentEntry.length}"
        holder.textViewWeight.text = "Weight: ${currentEntry.weight}"
        // Bind other fields as needed
    }

    abstract fun setFishEntries(fishEntries: MutableList<FishEntry>): QuerySnapshot?


}