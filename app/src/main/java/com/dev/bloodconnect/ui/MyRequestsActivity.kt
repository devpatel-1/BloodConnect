package com.dev.bloodconnect.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.bloodconnect.R
import com.dev.bloodconnect.repository.RequestRepository
import com.google.firebase.firestore.ListenerRegistration

class MyRequestsActivity : AppCompatActivity() {

    private val requestRepository = RequestRepository()
    private var listenerRegistration: ListenerRegistration? = null
    private lateinit var adapter: RequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_requests)

        val recyclerView = findViewById<RecyclerView>(R.id.requestsRecyclerView)
        val emptyText = findViewById<TextView>(R.id.emptyRequestsText)

        adapter = RequestAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        listenerRegistration = requestRepository.listenToMyRequests { requests ->
            adapter.updateRequests(requests)
            emptyText.visibility = if (requests.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }
}