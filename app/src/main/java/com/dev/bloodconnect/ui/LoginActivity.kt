package com.dev.bloodconnect.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dev.bloodconnect.repository.AuthRepository
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.dev.bloodconnect.R.layout.activity_login)

        val emailInput = findViewById<TextInputEditText>(com.dev.bloodconnect.R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(com.dev.bloodconnect.R.id.passwordInput)
        val loginButton = findViewById<Button>(com.dev.bloodconnect.R.id.loginButton)
        val progressBar = findViewById<ProgressBar>(com.dev.bloodconnect.R.id.loginProgress)
        val goToSignUp = findViewById<TextView>(com.dev.bloodconnect.R.id.goToSignUp)

        loginButton.setOnClickListener {
            val email = emailInput.text?.toString()?.trim().orEmpty()
            val password = passwordInput.text?.toString()?.trim().orEmpty()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = android.view.View.VISIBLE
            loginButton.isEnabled = false

            lifecycleScope.launch {
                val result = authRepository.login(email, password)
                progressBar.visibility = android.view.View.GONE
                loginButton.isEnabled = true

                result.onSuccess {
                    Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                    // We'll wire up navigation to the Dashboard in a later step
                }.onFailure { error ->
                    Toast.makeText(
                        this@LoginActivity,
                        "Login failed: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        goToSignUp.setOnClickListener {
            Toast.makeText(this, "Sign up screen coming soon", Toast.LENGTH_SHORT).show()
            // We'll wire up SignUpActivity in a later step
        }
    }
}
