package com.suryashaktiapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.suryashaktiapp.LoginActivity
import com.suryashaktiapp.databinding.FragmentProfileBinding
import com.suryashaktiapp.viewmodel.EnergyViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EnergyViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        loadProfileData()
        observeStats()

        // Logout Button
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        // Delete Account Button
        binding.btnDeleteAccount.setOnClickListener {
            showDeleteDialog()
        }
    }

    private fun loadProfileData() {
        val prefs = requireContext()
            .getSharedPreferences("surya_settings", Context.MODE_PRIVATE)

        val name = prefs.getString("username", "Solar User") ?: "Solar User"
        val location = prefs.getString("location", "Not set") ?: "Not set"
        val capacity = prefs.getString("panel_capacity", "2.0") ?: "2.0"
        val rate = prefs.getString("default_rate", "8.0") ?: "8.0"
        val email = auth.currentUser?.email ?: "Not logged in"

        binding.tvProfileName.text = name
        binding.tvProfileEmail.text = email
        binding.tvProfileLocation.text = "📍 $location"
        binding.tvPanelCapacity.text = "Panel Capacity: $capacity kW"
        binding.tvRatePerUnit.text = "Rate per Unit: ₹$rate/kWh"
        binding.tvMemberSince.text = "Member Since: May 2025"
    }

    private fun observeStats() {
        viewModel.allLogs.observe(viewLifecycleOwner) { logs ->
            binding.tvTotalLogs.text = logs.size.toString()
            val totalGenerated = logs.sumOf { it.generatedKwh.toDouble() }
            binding.tvTotalGenerated.text = "%.1f".format(totalGenerated)
        }

        viewModel.totalSavings.observe(viewLifecycleOwner) { savings ->
            binding.tvTotalSaved.text = "₹%.0f".format(savings ?: 0f)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Account")
            .setMessage("Are you sure? This will permanently delete your account!")
            .setPositiveButton("Delete") { _, _ ->
                deleteAccount()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        // Clear SharedPreferences
        requireContext()
            .getSharedPreferences("surya_settings", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        // Firebase logout
        auth.signOut()

        Toast.makeText(requireContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show()

        // Go to Login
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun deleteAccount() {
        auth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                requireContext()
                    .getSharedPreferences("surya_settings", Context.MODE_PRIVATE)
                    .edit().clear().apply()

                Toast.makeText(
                    requireContext(),
                    "Account deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}