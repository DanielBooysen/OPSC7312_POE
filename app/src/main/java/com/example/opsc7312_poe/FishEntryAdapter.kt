package com.example.opsc7312_poe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QuerySnapshot

class FishEntryAdapter(private var fishEntries: MutableList<FishEntry>) : RecyclerView.Adapter<FishEntryAdapter.FishEntryViewHolder>() {

    class FishEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewSpecies: TextView = itemView.findViewById(R.id.textViewSpecies)
        val textViewLength: TextView = itemView.findViewById(R.id.textViewLength)
        val textViewWeight: TextView = itemView.findViewById(R.id.textViewWeight)
        val textViewBait: TextView = itemView.findViewById(R.id.textViewBait)
        val textViewTimeOfDay: TextView = itemView.findViewById(R.id.textViewTimeofDay)
        val textViewTime: TextView = itemView.findViewById(R.id.textViewTime)
        val textViewWeather: TextView = itemView.findViewById(R.id.textViewWeather)
        val textViewLocation: TextView = itemView.findViewById(R.id.textViewLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishEntryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fish_entry, parent, false)
        return FishEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: FishEntryViewHolder, position: Int) {
        val currentEntry = fishEntries[position]
        holder.textViewSpecies.text = currentEntry.species
        holder.textViewLength.text = "Length: ${currentEntry.length}"
        holder.textViewWeight.text = "Weight: ${currentEntry.weight}"
        holder.textViewBait.text = "Bait Used: ${currentEntry.baitUsed}"
        holder.textViewTimeOfDay.text = "Time of Day: ${currentEntry.timeOfDay}"
        holder.textViewTime.text = "Time: ${currentEntry.time}"
        holder.textViewWeather.text = "Weather: ${currentEntry.weather}"
        holder.textViewLocation.text = "Location: ${currentEntry.location}"
    }

    override fun getItemCount(): Int = fishEntries.size

    // Method to update the list of fish entries
    fun setFishEntries(newFishEntries: List<FishEntry>) {
        fishEntries.clear()
        fishEntries.addAll(newFishEntries)
        notifyDataSetChanged()
    }
}
