package com.dev.bloodconnect.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dev.bloodconnect.R
import com.dev.bloodconnect.data.Request
import java.text.SimpleDateFormat
import java.util.Locale

class RequestAdapter(
    private var requests: List<Request>
) : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    class RequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val donorName: TextView =
            view.findViewById(R.id.requestDonorName)

        val bloodGroup: TextView =
            view.findViewById(R.id.requestBloodGroup)

        val status: TextView =
            view.findViewById(R.id.requestStatus)

        val statusMessage: TextView =
            view.findViewById(R.id.requestStatusMessage)

        val date: TextView =
            view.findViewById(R.id.requestDate)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequestViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_request,
                parent,
                false
            )

        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RequestViewHolder,
        position: Int
    ) {

        val request = requests[position]

        // Donor information
        holder.donorName.text = request.donorName

        holder.bloodGroup.text =
            "Blood Group: ${request.bloodGroup}"

        // Request status
        when (request.status.lowercase()) {

            "accepted" -> {

                holder.status.text =
                    "● Request Accepted"

                holder.statusMessage.text =
                    "The donor has accepted your blood request."

                holder.status.setTextColor(
                    0xFF2E7D32.toInt()
                )
            }

            "declined" -> {

                holder.status.text =
                    "● Request Declined"

                holder.statusMessage.text =
                    "The donor declined your blood request."

                holder.status.setTextColor(
                    0xFFC62828.toInt()
                )
            }

            else -> {

                holder.status.text =
                    "● Request Pending"

                holder.statusMessage.text =
                    "Waiting for the donor to respond."

                holder.status.setTextColor(
                    0xFFF57F17.toInt()
                )
            }
        }

        // Request date
        holder.date.text =
            formatDate(request.createdAt)
    }

    override fun getItemCount(): Int =
        requests.size

    fun updateRequests(
        newRequests: List<Request>
    ) {

        requests = newRequests

        notifyDataSetChanged()
    }

    private fun formatDate(
        timestamp: com.google.firebase.Timestamp?
    ): String {

        if (timestamp == null) {
            return "Requested: Unknown"
        }

        val formatter =
            SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.getDefault()
            )

        return "Requested: ${
            formatter.format(timestamp.toDate())
        }"
    }
}