package com.dev.bloodconnect.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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

    private var listenerRegistration:
            ListenerRegistration? = null

    private lateinit var adapter: DonorAdapter

    private var allDonors: List<User> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_dashboard
        )

        // -----------------------------------------
        // FIND VIEWS
        // -----------------------------------------

        val recyclerView =
            findViewById<RecyclerView>(
                R.id.donorRecyclerView
            )

        val searchInput =
            findViewById<TextInputEditText>(
                R.id.searchInput
            )

        val emptyStateText =
            findViewById<TextView>(
                R.id.emptyStateText
            )

        val statsText =
            findViewById<TextView>(
                R.id.statsText
            )

        val myProfileButton =
            findViewById<TextView>(
                R.id.myProfileButton
            )

        val myRequestsButton =
            findViewById<TextView>(
                R.id.myRequestsButton
            )

        val incomingRequestsButton =
            findViewById<TextView>(
                R.id.incomingRequestsButton
            )

        val logoutButton =
            findViewById<TextView>(
                R.id.logoutButton
            )

        // -----------------------------------------
        // DONOR ADAPTER
        // -----------------------------------------

        adapter = DonorAdapter(
            emptyList()
        ) { donor ->

            startActivity(
                DonorDetailActivity.newIntent(
                    context = this,
                    uid = donor.uid,
                    name = donor.name,
                    phone = donor.phone,
                    city = donor.city,
                    bloodGroup = donor.bloodGroup,
                    isAvailable =
                        donor.isActuallyAvailable()
                )
            )
        }

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter =
            adapter

        // -----------------------------------------
        // SEARCH
        // -----------------------------------------

        searchInput.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Nothing needed
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    filterDonors(
                        query = s?.toString().orEmpty(),
                        emptyStateText = emptyStateText
                    )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                    // Nothing needed
                }
            }
        )

        // -----------------------------------------
        // LOAD DONORS
        // -----------------------------------------

        listenerRegistration =
            donorRepository.listenToDonors { donors ->

                val currentUserId =
                    FirebaseAuth
                        .getInstance()
                        .currentUser
                        ?.uid

                /*
                 * Do not show the currently logged-in
                 * user in the donor list.
                 */
                allDonors =
                    donors.filter { donor ->
                        donor.uid != currentUserId
                    }

                // Apply current search
                filterDonors(
                    query = searchInput.text
                        ?.toString()
                        .orEmpty(),
                    emptyStateText = emptyStateText
                )

                // Update statistics
                statsText.text =
                    buildStatsText(allDonors)
            }

        // -----------------------------------------
        // MY PROFILE
        // -----------------------------------------

        myProfileButton.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        // -----------------------------------------
        // MY REQUESTS
        // -----------------------------------------

        myRequestsButton.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MyRequestsActivity::class.java
                )
            )
        }

        // -----------------------------------------
        // INCOMING REQUESTS
        // -----------------------------------------

        incomingRequestsButton.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    IncomingRequestsActivity::class.java
                )
            )
        }

        // -----------------------------------------
        // LOGOUT
        // -----------------------------------------

        logoutButton.setOnClickListener {

            FirebaseAuth
                .getInstance()
                .signOut()

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )

            finish()
        }
    }

    // -----------------------------------------
    // FILTER DONORS
    // -----------------------------------------

    private fun filterDonors(
        query: String,
        emptyStateText: TextView
    ) {

        /*
         * Normalize the search text:
         *
         * "  Mehsana  "
         * becomes
         * "mehsana"
         */
        val cleanQuery =
            query
                .trim()
                .replace(
                    Regex("\\s+"),
                    " "
                )
                .lowercase()

        val filtered =
            if (cleanQuery.isBlank()) {

                allDonors

            } else {

                allDonors.filter { donor ->

                    val bloodGroup =
                        donor.bloodGroup
                            .trim()
                            .lowercase()

                    val city =
                        donor.city
                            .trim()
                            .lowercase()

                    val name =
                        donor.name
                            .trim()
                            .lowercase()

                    /*
                     * Search can match:
                     * - Blood group
                     * - City
                     * - Name
                     */
                    bloodGroup.contains(
                        cleanQuery
                    ) ||
                            city.contains(
                                cleanQuery
                            ) ||
                            name.contains(
                                cleanQuery
                            )
                }
            }

        // Update RecyclerView
        adapter.updateDonors(
            filtered
        )

        // -----------------------------------------
        // EMPTY STATE
        // -----------------------------------------

        if (filtered.isEmpty()) {

            emptyStateText.visibility =
                View.VISIBLE

            emptyStateText.text =
                if (cleanQuery.isBlank()) {

                    "No donors found yet.\n" +
                            "Be the first to sign up!"

                } else {

                    "No donors found for \"$query\".\n" +
                            "Try a blood group, city, or name."
                }

        } else {

            emptyStateText.visibility =
                View.GONE
        }
    }

    // -----------------------------------------
    // DONOR STATISTICS
    // -----------------------------------------

    private fun buildStatsText(
        donors: List<User>
    ): String {

        if (donors.isEmpty()) {

            return "No donors yet"
        }

        val counts =
            donors
                .groupingBy {
                    it.bloodGroup
                }
                .eachCount()

        val breakdown =
            counts
                .entries
                .sortedBy {
                    it.key
                }
                .joinToString("  ") { (group, count) ->

                    "$group: $count"
                }

        return "${donors.size} donors • $breakdown"
    }

    // -----------------------------------------
    // CLEAN UP FIRESTORE LISTENER
    // -----------------------------------------

    override fun onDestroy() {

        super.onDestroy()

        listenerRegistration?.remove()
    }
}