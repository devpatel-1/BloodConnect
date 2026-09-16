package com.dev.bloodconnect.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
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

    private lateinit var availabilitySwitch: Switch
    private lateinit var availabilityStatus: TextView
    private lateinit var updateStatusText: TextView
    private lateinit var progressBar: ProgressBar

    private var isLoadingProfile = true
    private var isUpdatingAvailability = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        val nameText =
            findViewById<TextView>(
                R.id.profileName
            )

        val bloodGroupText =
            findViewById<TextView>(
                R.id.profileBloodGroup
            )

        val cityText =
            findViewById<TextView>(
                R.id.profileCity
            )

        val phoneText =
            findViewById<TextView>(
                R.id.profilePhone
            )

        availabilitySwitch =
            findViewById(
                R.id.availabilitySwitch
            )

        availabilityStatus =
            findViewById(
                R.id.availabilityStatus
            )

        updateStatusText =
            findViewById(
                R.id.updateStatusText
            )

        progressBar =
            findViewById(
                R.id.profileProgress
            )

        // --------------------------------------------------
        // LOAD PROFILE
        // --------------------------------------------------

        loadProfile(
            nameText,
            bloodGroupText,
            cityText,
            phoneText
        )

        // --------------------------------------------------
        // AVAILABILITY SWITCH
        // --------------------------------------------------

        availabilitySwitch.setOnCheckedChangeListener {
                _,
                isChecked ->

            if (isLoadingProfile ||
                isUpdatingAvailability
            ) {
                return@setOnCheckedChangeListener
            }

            updateAvailability(
                isChecked
            )
        }
    }

    // --------------------------------------------------
    // LOAD PROFILE FROM FIREBASE
    // --------------------------------------------------

    private fun loadProfile(
        nameText: TextView,
        bloodGroupText: TextView,
        cityText: TextView,
        phoneText: TextView
    ) {

        lifecycleScope.launch {

            isLoadingProfile = true

            progressBar.visibility =
                View.VISIBLE

            availabilitySwitch.isEnabled =
                false

            val result =
                authRepository.getCurrentUserProfile()

            progressBar.visibility =
                View.GONE

            result.onSuccess { user ->

                nameText.text =
                    user.name

                bloodGroupText.text =
                    user.bloodGroup

                cityText.text =
                    "City: ${user.city}"

                phoneText.text =
                    "Phone: ${user.phone}"

                /*
                 * Remove listener temporarily while
                 * setting the switch from Firebase.
                 */
                availabilitySwitch.setOnCheckedChangeListener(
                    null
                )

                availabilitySwitch.isChecked =
                    user.isAvailable

                updateAvailabilityStatus(
                    user.isAvailable
                )

                /*
                 * Restore the listener.
                 */
                availabilitySwitch.setOnCheckedChangeListener {
                        _,
                        isChecked ->

                    if (isLoadingProfile ||
                        isUpdatingAvailability
                    ) {
                        return@setOnCheckedChangeListener
                    }

                    updateAvailability(
                        isChecked
                    )
                }

                isLoadingProfile = false

                availabilitySwitch.isEnabled =
                    true

            }.onFailure { error ->

                isLoadingProfile = false

                availabilitySwitch.isEnabled =
                    true

                Toast.makeText(
                    this@ProfileActivity,
                    "Failed to load profile: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // --------------------------------------------------
    // UPDATE AVAILABILITY
    // --------------------------------------------------

    private fun updateAvailability(
        isAvailable: Boolean
    ) {

        isUpdatingAvailability = true

        availabilitySwitch.isEnabled =
            false

        updateStatusText.visibility =
            View.GONE

        lifecycleScope.launch {

            val result =
                authRepository.updateAvailability(
                    isAvailable
                )

            result.onSuccess {

                updateAvailabilityStatus(
                    isAvailable
                )

                updateStatusText.visibility =
                    View.VISIBLE

                updateStatusText.text =
                    if (isAvailable) {
                        "You are now marked as available."
                    } else {
                        "You are now marked as unavailable."
                    }

            }.onFailure {

                /*
                 * Restore the previous state if
                 * Firebase update failed.
                 */
                availabilitySwitch.setOnCheckedChangeListener(
                    null
                )

                availabilitySwitch.isChecked =
                    !isAvailable

                availabilitySwitch.setOnCheckedChangeListener {
                        _,
                        checked ->

                    if (isLoadingProfile ||
                        isUpdatingAvailability
                    ) {
                        return@setOnCheckedChangeListener
                    }

                    updateAvailability(
                        checked
                    )
                }

                updateAvailabilityStatus(
                    !isAvailable
                )

                Toast.makeText(
                    this@ProfileActivity,
                    "Failed to update availability.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            isUpdatingAvailability = false

            availabilitySwitch.isEnabled =
                true
        }
    }

    // --------------------------------------------------
    // UPDATE STATUS TEXT
    // --------------------------------------------------

    private fun updateAvailabilityStatus(
        isAvailable: Boolean
    ) {

        availabilityStatus.visibility =
            View.VISIBLE

        if (isAvailable) {

            availabilityStatus.text =
                "● Available to donate"

            availabilityStatus.setTextColor(
                getColor(
                    android.R.color.holo_green_dark
                )
            )

        } else {

            availabilityStatus.text =
                "● Currently unavailable"

            availabilityStatus.setTextColor(
                getColor(
                    android.R.color.holo_red_dark
                )
            )
        }
    }
}