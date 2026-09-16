package com.dev.bloodconnect.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.bloodconnect.R
import com.dev.bloodconnect.data.Request
import com.dev.bloodconnect.repository.RequestRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class IncomingRequestsActivity : AppCompatActivity() {

    private val requestRepository =
        RequestRepository()

    private var listenerRegistration:
            ListenerRegistration? = null

    private lateinit var adapter:
            IncomingRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_incoming_requests
        )

        val recyclerView =
            findViewById<RecyclerView>(
                R.id.incomingRequestsRecyclerView
            )

        val emptyText =
            findViewById<TextView>(
                R.id.emptyIncomingRequestsText
            )

        adapter = IncomingRequestAdapter(
            emptyList(),

            onAccept = { request ->
                updateRequest(
                    request,
                    "accepted"
                )
            },

            onDecline = { request ->
                updateRequest(
                    request,
                    "declined"
                )
            }
        )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter = adapter

        listenerRegistration =
            requestRepository
                .listenToIncomingRequests { requests ->

                    adapter.updateRequests(
                        requests
                    )

                    emptyText.visibility =
                        if (requests.isEmpty()) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                }
    }

    private fun updateRequest(
        request: Request,
        status: String
    ) {

        lifecycleScope.launch {

            val result =
                requestRepository.updateRequestStatus(
                    requestId = request.id,
                    newStatus = status
                )

            result.onSuccess {

                Toast.makeText(
                    this@IncomingRequestsActivity,
                    if (status == "accepted") {
                        "Request accepted"
                    } else {
                        "Request declined"
                    },
                    Toast.LENGTH_SHORT
                ).show()

            }.onFailure { error ->

                Toast.makeText(
                    this@IncomingRequestsActivity,
                    "Failed: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        listenerRegistration?.remove()
    }
}