package com.suryashaktiapp.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.suryashaktiapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSettings()

        binding.btnSaveSettings.setOnClickListener {
            saveSettings()
        }
    }

    private fun loadSettings() {
        val prefs = requireContext()
            .getSharedPreferences("surya_settings", Context.MODE_PRIVATE)
        binding.etUsername.setText(prefs.getString("username", ""))
        binding.etPanelCapacity.setText(prefs.getString("panel_capacity", "2.0"))
        binding.etDefaultRate.setText(prefs.getString("default_rate", "8.0"))
        binding.etBatteryCapacity.setText(prefs.getString("battery_capacity", "5.0"))
        binding.etLocation.setText(prefs.getString("location", ""))
    }

    private fun saveSettings() {
        val prefs = requireContext()
            .getSharedPreferences("surya_settings", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("username", binding.etUsername.text.toString())
            putString("panel_capacity", binding.etPanelCapacity.text.toString())
            putString("default_rate", binding.etDefaultRate.text.toString())
            putString("battery_capacity", binding.etBatteryCapacity.text.toString())
            putString("location", binding.etLocation.text.toString())
            apply()
        }
        Toast.makeText(requireContext(), "✅ Settings Saved!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}