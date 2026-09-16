package com.dev.bloodconnect.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dev.bloodconnect.R
import com.dev.bloodconnect.repository.AuthRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val nameInput =
            findViewById<TextInputEditText>(R.id.nameInput)

        val emailInput =
            findViewById<TextInputEditText>(R.id.emailInput)

        val passwordInput =
            findViewById<TextInputEditText>(R.id.passwordInput)

        val phoneInput =
            findViewById<TextInputEditText>(R.id.phoneInput)

        val bloodGroupInput =
            findViewById<AutoCompleteTextView>(R.id.bloodGroupInput)

        val cityInput =
            findViewById<TextInputEditText>(R.id.cityInput)

        val signUpButton =
            findViewById<Button>(R.id.signUpButton)

        val progressBar =
            findViewById<ProgressBar>(R.id.signUpProgress)

        val nameLayout =
            findViewById<TextInputLayout>(R.id.nameLayout)

        val emailLayout =
            findViewById<TextInputLayout>(R.id.emailLayout)

        val passwordLayout =
            findViewById<TextInputLayout>(R.id.passwordLayout)

        val phoneLayout =
            findViewById<TextInputLayout>(R.id.phoneLayout)

        val bloodGroupLayout =
            findViewById<TextInputLayout>(R.id.bloodGroupLayout)

        val cityLayout =
            findViewById<TextInputLayout>(R.id.cityLayout)

        // Blood group dropdown
        val bloodGroups = arrayOf(
            "A+",
            "A-",
            "B+",
            "B-",
            "AB+",
            "AB-",
            "O+",
            "O-"
        )

        val bloodGroupAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            bloodGroups
        )

        bloodGroupInput.setAdapter(bloodGroupAdapter)

        bloodGroupInput.setOnClickListener {
            bloodGroupInput.showDropDown()
        }

        // Clear errors when user starts correcting fields
        nameInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                nameLayout.error = null
            }
        }

        emailInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                emailLayout.error = null
            }
        }

        passwordInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                passwordLayout.error = null
            }
        }

        phoneInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                phoneLayout.error = null
            }
        }

        bloodGroupInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                bloodGroupLayout.error = null
            }
        }

        cityInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                cityLayout.error = null
            }
        }

        signUpButton.setOnClickListener {

            // Clear previous errors
            nameLayout.error = null
            emailLayout.error = null
            passwordLayout.error = null
            phoneLayout.error = null
            bloodGroupLayout.error = null
            cityLayout.error = null

            val name =
                nameInput.text?.toString()?.trim().orEmpty()

            val email =
                emailInput.text?.toString()?.trim().orEmpty()

            // Do NOT trim password because spaces can technically be
            // part of a password.
            val password =
                passwordInput.text?.toString().orEmpty()

            val phone =
                phoneInput.text?.toString()?.trim().orEmpty()

            val bloodGroup =
                bloodGroupInput.text?.toString()?.trim()?.uppercase().orEmpty()

            val city =
                cityInput.text?.toString()?.trim().orEmpty()

            var isValid = true

            // Name validation
            if (name.isEmpty()) {
                nameLayout.error = "Please enter your full name"
                isValid = false
            } else if (name.length < 2) {
                nameLayout.error = "Name must contain at least 2 characters"
                isValid = false
            }

            // Email validation
            if (email.isEmpty()) {
                emailLayout.error = "Please enter your email"
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.error = "Please enter a valid email address"
                isValid = false
            }

            // Password validation
            if (password.isEmpty()) {
                passwordLayout.error = "Please enter a password"
                isValid = false
            } else if (password.length < 6) {
                passwordLayout.error =
                    "Password must be at least 6 characters"
                isValid = false
            }

            // Phone validation
            if (phone.isEmpty()) {
                phoneLayout.error = "Please enter your phone number"
                isValid = false
            } else if (!phone.matches(Regex("^[6-9][0-9]{9}$"))) {
                phoneLayout.error =
                    "Enter a valid 10-digit Indian mobile number"
                isValid = false
            }

            // Blood group validation
            if (bloodGroup.isEmpty()) {
                bloodGroupLayout.error =
                    "Please select your blood group"
                isValid = false
            } else if (!bloodGroups.contains(bloodGroup)) {
                bloodGroupLayout.error =
                    "Please select a valid blood group"
                isValid = false
            }

            // City validation
            if (city.isEmpty()) {
                cityLayout.error = "Please enter your city"
                isValid = false
            } else if (city.length < 2) {
                cityLayout.error =
                    "City name must contain at least 2 characters"
                isValid = false
            }

            if (!isValid) {
                return@setOnClickListener
            }

            // Start signup
            progressBar.visibility = View.VISIBLE
            signUpButton.isEnabled = false

            lifecycleScope.launch {

                val result = authRepository.signUp(
                    email = email,
                    password = password,
                    name = name,
                    phone = phone,
                    bloodGroup = bloodGroup,
                    city = city
                )

                progressBar.visibility = View.GONE
                signUpButton.isEnabled = true

                result.onSuccess {

                    Toast.makeText(
                        this@SignUpActivity,
                        "Account created successfully!",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(
                            this@SignUpActivity,
                            DashboardActivity::class.java
                        )
                    )

                    finish()

                }.onFailure { error ->

                    Toast.makeText(
                        this@SignUpActivity,
                        "Signup failed: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}