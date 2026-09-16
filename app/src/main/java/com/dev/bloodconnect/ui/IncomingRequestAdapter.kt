package com.dev.bloodconnect.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dev.bloodconnect.R
import com.dev.bloodconnect.data.Request
import java.text.SimpleDateFormat
import java.util.Locale

class IncomingRequestAdapter(
    private var requests: List<Request>,
    private val onAccept: (Request) -> Unit,
    private val onDecline: (Request) -> Unit
) : RecyclerView.Adapter<IncomingRequestAdapter.RequestViewHolder>() {

    class RequestViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val requesterName: TextView =
            view.findViewById(R.id.incomingRequesterName)

        val bloodGroup: TextView =
            view.findViewById(R.id.incomingBloodGroup)

        val status: TextView =
            view.findViewById(R.id.incomingRequestStatus)

        val date: TextView =
            view.findViewById(R.id.incomingRequestDate)

        val acceptButton: Button =
            view.findViewById(R.id.acceptRequestButton)

        val declineButton: Button =
            view.findViewById(R.id.declineRequestButton)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequestViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_incoming_request,
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

        holder.requesterName.text =
            request.requesterName

        holder.bloodGroup.text =
            "Blood Group: ${request.bloodGroup}"

        holder.status.text =
            "Status: ${
                request.status.replaceFirstChar {
                    it.uppercase()
                }
            }"

        holder.date.text =
            formatDate(request.createdAt)

        val isPending =
            request.status.lowercase() == "pending"

        holder.acceptButton.visibility =
            if (isPending) View.VISIBLE else View.GONE

        holder.declineButton.visibility =
            if (isPending) View.VISIBLE else View.GONE

        holder.status.setTextColor(
            when (request.status.lowercase()) {

                "accepted" ->
                    0xFF2E7D32.toInt()

                "declined" ->
                    0xFFC62828.toInt()

                else ->
                    0xFFF57F17.toInt()
            }
        )

        holder.acceptButton.setOnClickListener {
            onAccept(request)
        }

        holder.declineButton.setOnClickListener {
            onDecline(request)
        }
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

        val formatter = SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            Locale.getDefault()
        )

        return "Requested: ${
            formatter.format(timestamp.toDate())
        }"
    }
}