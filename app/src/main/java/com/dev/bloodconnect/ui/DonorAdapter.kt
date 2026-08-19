package com.dev.bloodconnect.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dev.bloodconnect.R
import com.dev.bloodconnect.data.User

/**
 * Adapter for showing the list of donors in a RecyclerView.
 * onDonorClick lets the Dashboard react when a donor row is tapped
 * (we'll use this later to open the Donor Detail screen).
 */
class DonorAdapter(
    private var donors: List<User>,
    private val onDonorClick: (User) -> Unit
) : RecyclerView.Adapter<DonorAdapter.DonorViewHolder>() {

    class DonorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bloodGroup: TextView = view.findViewById(R.id.donorBloodGroup)
        val name: TextView = view.findViewById(R.id.donorName)
        val city: TextView = view.findViewById(R.id.donorCity)
        val availability: TextView = view.findViewById(R.id.donorAvailability)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_donor, parent, false)
        return DonorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonorViewHolder, position: Int) {
        val donor = donors[position]
        holder.bloodGroup.text = donor.bloodGroup
        holder.name.text = donor.name
        holder.city.text = donor.city

        val available = donor.isActuallyAvailable()
        holder.availability.text = if (available) "Available" else "Not available"
        holder.availability.setTextColor(
            if (available) 0xFF2E7D32.toInt() else 0xFF999999.toInt()
        )

        holder.itemView.setOnClickListener { onDonorClick(donor) }
    }

    override fun getItemCount(): Int = donors.size

    /** Call this to refresh the list when new data arrives from Firestore. */
    fun updateDonors(newDonors: List<User>) {
        donors = newDonors
        notifyDataSetChanged()
    }
}