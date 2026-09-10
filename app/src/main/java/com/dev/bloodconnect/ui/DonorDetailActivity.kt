package com.dev.bloodconnect.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

        /** Helper to build the Intent with all donor info attached. */
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
        val isAvailable = intent.getBooleanExtra(EXTRA_DONOR_AVAILABLE, true)

        findViewById<TextView>(R.id.detailBloodGroup).text = bloodGroup
        findViewById<TextView>(R.id.detailName).text = name
        findViewById<TextView>(R.id.detailCity).text = city
        findViewById<TextView>(R.id.detailPhone).text = "Phone: $phone"

        val availabilityText = findViewById<TextView>(R.id.detailAvailability)
        availabilityText.text = if (isAvailable) "Available" else "Not available"

        val sendRequestButton = findViewById<Button>(R.id.sendRequestButton)
        val requestStatusText = findViewById<TextView>(R.id.requestStatusText)

        sendRequestButton.setOnClickListener {
            sendRequestButton.isEnabled = false

            lifecycleScope.launch {
                val myProfile = authRepository.getCurrentUserProfile().getOrNull()
                val myName = myProfile?.name ?: "A user"

                val result = requestRepository.sendRequest(
                    donorId = uid,
                    donorName = name,
                    requesterName = myName,
                    bloodGroup = bloodGroup
                )

                result.onSuccess {
                    requestStatusText.visibility = android.view.View.VISIBLE
                    requestStatusText.text = "Request sent to $name!"
                    Toast.makeText(this@DonorDetailActivity, "Request sent!", Toast.LENGTH_SHORT).show()
                }.onFailure { error ->
                    sendRequestButton.isEnabled = true
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