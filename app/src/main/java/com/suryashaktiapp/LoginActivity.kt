package com.suryashaktiapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.suryashaktiapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // If already logged in go to MainActivity
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding.btnLogin.setOnClickListener {
            val name = binding.etLoginName.text.toString().trim()
            val password = binding.etLoginPin.text.toString().trim()

            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter name and password",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(
                    this,
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Generate same email format used during registration
            val email = "${name.replace(" ", "").lowercase()}@suryashakti.com"

            // Show loading
            binding.btnLogin.isEnabled = false
            binding.btnLogin.text = "Signing in..."

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "SIGN IN"

                    if (task.isSuccessful) {
                        // Save name locally
                        getSharedPreferences("surya_settings", MODE_PRIVATE)
                            .edit()
                            .putString("username", name)
                            .apply()

                        Toast.makeText(
                            this,
                            "Welcome back $name! ☀️",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "❌ Invalid name or password!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, ProfileSetupActivity::class.java))
        }
    }
}