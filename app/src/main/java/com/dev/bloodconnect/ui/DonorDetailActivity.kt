package com.dev.bloodconnect.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dev.bloodconnect.R
import com.dev.bloodconnect.repository.AuthRepository
import com.dev.bloodconnect.repository.RequestRepository
import kotlinx.coroutines.launch

class DonorDetailActivity : AppCompatActivity() {

    private val requestRepository = RequestRepository()
    private val authRepository = AuthRepository()

    companion object {
        private const val EXTRA_DONOR_UID = "donor_uid"
        private const val EXTRA_DONOR_NAME = "donor_name"
        private const val EXTRA_DONOR_PHONE = "donor_phone"
        private const val EXTRA_DONOR_CITY = "donor_city"
        private const val EXTRA_DONOR_BLOOD_GROUP = "donor_blood_group"
        private const val EXTRA_DONOR_AVAILABLE = "donor_available"

        fun newIntent(
            context: Context,
            uid: String,
            name: String,
            phone: String,
            city: String,
            bloodGroup: String,
            isAvailable: Boolean
        ): Intent {
            return Intent(context, DonorDetailActivity::class.java).apply {
                putExtra(EXTRA_DONOR_UID, uid)
                putExtra(EXTRA_DONOR_NAME, name)
                putExtra(EXTRA_DONOR_PHONE, phone)
                putExtra(EXTRA_DONOR_CITY, city)
                putExtra(EXTRA_DONOR_BLOOD_GROUP, bloodGroup)
                putExtra(EXTRA_DONOR_AVAILABLE, isAvailable)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_detail)

        val uid = intent.getStringExtra(EXTRA_DONOR_UID).orEmpty()
        val name = intent.getStringExtra(EXTRA_DONOR_NAME).orEmpty()
        val phone = intent.getStringExtra(EXTRA_DONOR_PHONE).orEmpty()
        val city = intent.getStringExtra(EXTRA_DONOR_CITY).orEmpty()
        val bloodGroup = intent.getStringExtra(EXTRA_DONOR_BLOOD_GROUP).orEmpty()
        val isAvailable = intent.getBooleanExtra(
            EXTRA_DONOR_AVAILABLE,
            false
        )

        val detailBloodGroup =
            findViewById<TextView>(R.id.detailBloodGroup)

        val detailName =
            findViewById<TextView>(R.id.detailName)

        val detailCity =
            findViewById<TextView>(R.id.detailCity)

        val detailPhone =
            findViewById<TextView>(R.id.detailPhone)

        val detailAvailability =
            findViewById<TextView>(R.id.detailAvailability)

        val sendRequestButton =
            findViewById<Button>(R.id.sendRequestButton)

        val requestStatusText =
            findViewById<TextView>(R.id.requestStatusText)

        detailBloodGroup.text = bloodGroup
        detailName.text = name
        detailCity.text = city
        detailPhone.text = "Phone: $phone"

        // -----------------------------------------
        // DONOR AVAILABILITY
        // -----------------------------------------

        if (isAvailable) {

            detailAvailability.text = "Available"
            detailAvailability.setTextColor(
                getColor(android.R.color.holo_green_dark)
            )

            sendRequestButton.isEnabled = true
            sendRequestButton.text = "Checking request..."

            // -----------------------------------------
            // CHECK EXISTING PENDING REQUEST
            // -----------------------------------------

            lifecycleScope.launch {

                val pendingResult =
                    requestRepository.hasPendingRequest(uid)

                pendingResult.onSuccess { hasPending ->

                    if (hasPending) {

                        // User already requested this donor
                        sendRequestButton.isEnabled = false
                        sendRequestButton.text = "Request Pending"

                        requestStatusText.visibility = View.VISIBLE
                        requestStatusText.text =
                            "You already have a pending request to this donor."

                        requestStatusText.setTextColor(
                            getColor(android.R.color.holo_orange_dark)
                        )

                    } else {

                        // No pending request
                        sendRequestButton.isEnabled = true
                        sendRequestButton.text = "Send Request"

                        requestStatusText.visibility = View.GONE
                    }
                }

                pendingResult.onFailure {

                    // If checking fails, allow the user to try.
                    // sendRequest() still performs its own duplicate check.
                    sendRequestButton.isEnabled = true
                    sendRequestButton.text = "Send Request"
                }
            }

        } else {

            // -----------------------------------------
            // DONOR NOT AVAILABLE
            // -----------------------------------------

            detailAvailability.text = "Not available"

            detailAvailability.setTextColor(
                getColor(android.R.color.holo_red_dark)
            )

            sendRequestButton.isEnabled = false
            sendRequestButton.text = "Donor Unavailable"

            requestStatusText.visibility = View.VISIBLE
            requestStatusText.text =
                "This donor is currently unavailable."

            requestStatusText.setTextColor(
                getColor(android.R.color.holo_red_dark)
            )
        }

        // -----------------------------------------
        // SEND REQUEST
        // -----------------------------------------

        sendRequestButton.setOnClickListener {

            // Extra availability protection
            if (!isAvailable) {

                Toast.makeText(
                    this,
                    "This donor is currently unavailable.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            sendRequestButton.isEnabled = false
            sendRequestButton.text = "Sending..."

            lifecycleScope.launch {

                val myProfile =
                    authRepository
                        .getCurrentUserProfile()
                        .getOrNull()

                val myName =
                    myProfile?.name ?: "A user"

                val result =
                    requestRepository.sendRequest(
                        donorId = uid,
                        donorName = name,
                        requesterName = myName,
                        bloodGroup = bloodGroup
                    )

                result.onSuccess {

                    requestStatusText.visibility = View.VISIBLE
                    requestStatusText.text =
                        "Request sent to $name!"

                    requestStatusText.setTextColor(
                        getColor(android.R.color.holo_green_dark)
                    )

                    sendRequestButton.text =
                        "Request Sent ✓"

                    sendRequestButton.isEnabled = false

                    Toast.makeText(
                        this@DonorDetailActivity,
                        "Request sent!",
                        Toast.LENGTH_SHORT
                    ).show()

                }.onFailure { error ->

                    sendRequestButton.isEnabled = true
                    sendRequestButton.text = "Send Request"

                    Toast.makeText(
                        this@DonorDetailActivity,
                        "Failed to send request: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}