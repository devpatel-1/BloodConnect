package com.dev.bloodconnect.ui

import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dev.bloodconnect.R
import com.dev.bloodconnect.repository.AuthRepository
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()
    private var isLoadingInitialData = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val nameText = findViewById<TextView>(R.id.profileName)
        val bloodGroupText = findViewById<TextView>(R.id.profileBloodGroup)
        val cityText = findViewById<TextView>(R.id.profileCity)
        val phoneText = findViewById<TextView>(R.id.profilePhone)
        val availabilitySwitch = findViewById<Switch>(R.id.availabilitySwitch)
        val updateStatusText = findViewById<TextView>(R.id.updateStatusText)

        lifecycleScope.launch {
            val result = authRepository.getCurrentUserProfile()
            result.onSuccess { user ->
                nameText.text = user.name
                bloodGroupText.text = "Blood Group: ${user.bloodGroup}"
                cityText.text = "City: ${user.city}"
                phoneText.text = "Phone: ${user.phone}"
                availabilitySwitch.isChecked = user.isAvailable
                isLoadingInitialData = false
            }.onFailure {
                Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        }

        availabilitySwitch.setOnCheckedChangeListener { _, isChecked ->
            // Skip firing an update while we're still setting the initial
            // switch state from Firestore data (avoids a redundant write).
            if (isLoadingInitialData) return@setOnCheckedChangeListener

            lifecycleScope.launch {
                val result = authRepository.updateAvailability(isChecked)
                result.onSuccess {
                    updateStatusText.visibility = android.view.View.VISIBLE
                    updateStatusText.text = if (isChecked) "You're marked as available." else "You're marked as unavailable."
                }.onFailure {
                    Toast.makeText(this@ProfileActivity, "Failed to update", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}