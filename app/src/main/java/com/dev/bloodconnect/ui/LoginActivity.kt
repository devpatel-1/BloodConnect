package com.dev.bloodconnect.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dev.bloodconnect.R
import com.dev.bloodconnect.repository.AuthRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If user is already logged in, open Dashboard
        if (authRepository.currentUserId() != null) {
            openDashboard()
            return
        }

        setContentView(R.layout.activity_login)

        val emailInput =
            findViewById<TextInputEditText>(R.id.emailInput)

        val passwordInput =
            findViewById<TextInputEditText>(R.id.passwordInput)

        val loginButton =
            findViewById<Button>(R.id.loginButton)

        val progressBar =
            findViewById<ProgressBar>(R.id.loginProgress)

        val goToSignUp =
            findViewById<TextView>(R.id.goToSignUp)

        val forgotPasswordButton =
            findViewById<TextView>(R.id.forgotPasswordButton)

        // -----------------------------------------
        // LOGIN
        // -----------------------------------------

        loginButton.setOnClickListener {

            val email =
                emailInput.text
                    ?.toString()
                    ?.trim()
                    .orEmpty()

            // Do NOT trim password
            val password =
                passwordInput.text
                    ?.toString()
                    .orEmpty()

            if (email.isEmpty()) {

                emailInput.error =
                    "Please enter your email"

                emailInput.requestFocus()

                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS
                    .matcher(email)
                    .matches()
            ) {

                emailInput.error =
                    "Please enter a valid email address"

                emailInput.requestFocus()

                return@setOnClickListener
            }

            if (password.isEmpty()) {

                passwordInput.error =
                    "Please enter your password"

                passwordInput.requestFocus()

                return@setOnClickListener
            }

            if (password.length < 6) {

                passwordInput.error =
                    "Password must be at least 6 characters"

                passwordInput.requestFocus()

                return@setOnClickListener
            }

            emailInput.error = null
            passwordInput.error = null

            progressBar.visibility = View.VISIBLE
            loginButton.isEnabled = false
            forgotPasswordButton.isEnabled = false
            goToSignUp.isEnabled = false

            lifecycleScope.launch {

                val result =
                    authRepository.login(
                        email = email,
                        password = password
                    )

                progressBar.visibility = View.GONE
                loginButton.isEnabled = true
                forgotPasswordButton.isEnabled = true
                goToSignUp.isEnabled = true

                result.onSuccess {

                    Toast.makeText(
                        this@LoginActivity,
                        "Login successful!",
                        Toast.LENGTH_SHORT
                    ).show()

                    openDashboard()

                }.onFailure { error ->

                    Toast.makeText(
                        this@LoginActivity,
                        "Login failed: ${getLoginErrorMessage(error)}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // -----------------------------------------
        // FORGOT PASSWORD
        // -----------------------------------------

        forgotPasswordButton.setOnClickListener {

            val email =
                emailInput.text
                    ?.toString()
                    ?.trim()
                    .orEmpty()

            if (email.isEmpty()) {

                emailInput.error =
                    "Enter your email first"

                emailInput.requestFocus()

                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS
                    .matcher(email)
                    .matches()
            ) {

                emailInput.error =
                    "Please enter a valid email address"

                emailInput.requestFocus()

                return@setOnClickListener
            }

            emailInput.error = null

            forgotPasswordButton.isEnabled = false

            firebaseAuth
                .sendPasswordResetEmail(email)
                .addOnSuccessListener {

                    forgotPasswordButton.isEnabled = true

                    Toast.makeText(
                        this,
                        "Password reset email sent!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener { error ->

                    forgotPasswordButton.isEnabled = true

                    Toast.makeText(
                        this,
                        "Failed: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        // -----------------------------------------
        // SIGN UP
        // -----------------------------------------

        goToSignUp.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SignUpActivity::class.java
                )
            )
        }
    }

    // -----------------------------------------
    // OPEN DASHBOARD
    // -----------------------------------------

    private fun openDashboard() {

        val intent =
            Intent(
                this,
                DashboardActivity::class.java
            )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)

        finish()
    }

    // -----------------------------------------
    // FRIENDLY LOGIN ERRORS
    // -----------------------------------------

    private fun getLoginErrorMessage(
        error: Throwable
    ): String {

        val message =
            error.message
                ?.lowercase()
                .orEmpty()

        return when {

            message.contains("password is invalid") ||
                    message.contains("invalid credential") ||
                    message.contains("invalid-credential") ->
                "Incorrect email or password"

            message.contains("no user record") ||
                    message.contains("user-not-found") ->
                "No account found with this email"

            message.contains("network") ->
                "Please check your internet connection"

            else ->
                error.message
                    ?: "Something went wrong"
        }
    }
}