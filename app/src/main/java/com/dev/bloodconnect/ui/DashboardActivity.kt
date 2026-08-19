package com.dev.bloodconnect.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.bloodconnect.R
import com.dev.bloodconnect.data.User
import com.dev.bloodconnect.repository.DonorRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration

class DashboardActivity : AppCompatActivity() {

    private val donorRepository = DonorRepository()
    private var listenerRegistration: ListenerRegistration? = null

    private lateinit var adapter: DonorAdapter
    private var allDonors: List<User> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val recyclerView = findViewById<RecyclerView>(R.id.donorRecyclerView)
        val searchInput = findViewById<TextInputEditText>(R.id.searchInput)
        val emptyStateText = findViewById<TextView>(R.id.emptyStateText)

        adapter = DonorAdapter(emptyList()) { donor ->
            startActivity(
                DonorDetailActivity.newIntent(
                    context = this,
                    uid = donor.uid,
                    name = donor.name,
                    phone = donor.phone,
                    city = donor.city,
                    bloodGroup = donor.bloodGroup,
                    isAvailable = donor.isActuallyAvailable()
                )
            )
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterDonors(s?.toString().orEmpty(), emptyStateText)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val statsText = findViewById<TextView>(R.id.statsText)

        listenerRegistration = donorRepository.listenToDonors { donors ->
            allDonors = donors
            filterDonors(searchInput.text?.toString().orEmpty(), emptyStateText)
            statsText.text = buildStatsText(donors)
        }


        findViewById<TextView>(R.id.myRequestsButton).setOnClickListener {
            startActivity(Intent(this, MyRequestsActivity::class.java))
        }

        findViewById<TextView>(R.id.logoutButton).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<TextView>(R.id.myProfileButton).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun filterDonors(query: String, emptyStateText: TextView) {
        val filtered = if (query.isBlank()) {
            allDonors
        } else {
            allDonors.filter { donor ->
                donor.bloodGroup.contains(query, ignoreCase = true) ||
                        donor.city.contains(query, ignoreCase = true) ||
                        donor.name.contains(query, ignoreCase = true)
            }
        }
        adapter.updateDonors(filtered)
        emptyStateText.visibility = if (filtered.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun buildStatsText(donors: List<User>): String {
        if (donors.isEmpty()) return "No donors yet"
        val counts = donors.groupingBy { it.bloodGroup }.eachCount()
        val breakdown = counts.entries.joinToString("  ") { (group, count) -> "$group: $count" }
        return "${donors.size} donors • $breakdown"
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }
}