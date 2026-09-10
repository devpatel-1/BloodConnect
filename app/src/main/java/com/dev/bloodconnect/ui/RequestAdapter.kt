package com.dev.bloodconnect.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dev.bloodconnect.R
import com.dev.bloodconnect.data.Request

class RequestAdapter(private var requests: List<Request>) :
    RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    class RequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val donorName: TextView = view.findViewById(R.id.requestDonorName)
        val bloodGroup: TextView = view.findViewById(R.id.requestBloodGroup)
        val status: TextView = view.findViewById(R.id.requestStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requests[position]
        holder.donorName.text = request.donorName
        holder.bloodGroup.text = "Blood Group: ${request.bloodGroup}"
        holder.status.text = "Status: ${request.status.replaceFirstChar { it.uppercase() }}"

        val color = when (request.status) {
            "accepted" -> 0xFF2E7D32.toInt()
            "declined" -> 0xFFC62828.toInt()
            else -> 0xFFF57F17.toInt()
        }
        holder.status.setTextColor(color)
    }

    override fun getItemCount(): Int = requests.size

    fun updateRequests(newRequests: List<Request>) {
        requests = newRequests
        notifyDataSetChanged()
    }
}