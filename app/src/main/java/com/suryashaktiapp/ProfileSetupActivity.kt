package com.suryashaktiapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.suryashaktiapp.databinding.ActivityProfileSetupBinding

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnStart.setOnClickListener {
            val name = binding.etSetupName.text.toString().trim()
            val location = binding.etSetupLocation.text.toString().trim()
            val capacity = binding.etSetupCapacity.text.toString().trim()
            val rate = binding.etSetupRate.text.toString().trim()
            val password = binding.etSetupPin.text.toString().trim()

            // Validate
            if (name.isEmpty()) {
                Toast.makeText(this,
                    "Please enter your name",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this,
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Generate email from name
            val email = "${name.replace(" ", "").lowercase()}@suryashakti.com"

            // Show loading
            binding.btnStart.isEnabled = false
            binding.btnStart.text = "Creating account..."

            // Create Firebase Auth account
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid

                        // Save profile to Firestore
                        val userProfile = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "location" to location,
                            "panelCapacity" to (capacity.ifEmpty { "2.0" }),
                            "defaultRate" to (rate.ifEmpty { "8.0" }),
                            "createdAt" to System.currentTimeMillis()
                        )

                        userId?.let {
                            db.collection("users")
                                .document(it)
                                .set(userProfile)
                                .addOnSuccessListener {
                                    // Save name locally
                                    getSharedPreferences(
                                        "surya_settings",
                                        MODE_PRIVATE
                                    ).edit()
                                        .putString("username", name)
                                        .putString("user_email", email)
                                        .apply()

                                    Toast.makeText(
                                        this,
                                        "Welcome $name! ☀️",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    startActivity(
                                        Intent(this, MainActivity::class.java)
                                    )
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    binding.btnStart.isEnabled = true
                                    binding.btnStart.text = "🚀 START MY SOLAR JOURNEY"
                                    Toast.makeText(
                                        this,
                                        "Error: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else {
                        binding.btnStart.isEnabled = true
                        binding.btnStart.text = "🚀 START MY SOLAR JOURNEY"
                        Toast.makeText(
                            this,
                            "❌ ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}