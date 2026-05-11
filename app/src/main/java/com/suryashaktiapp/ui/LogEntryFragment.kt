package com.suryashaktiapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.suryashaktiapp.R
import com.suryashaktiapp.databinding.FragmentLogEntryBinding
import com.suryashaktiapp.viewmodel.EnergyViewModel

class LogEntryFragment : Fragment() {

    private var _binding: FragmentLogEntryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EnergyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rbSunny.isChecked = true

        binding.seekbarBattery.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                    binding.tvBatteryValue.text = "$progress%"
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }
        )

        binding.btnSimulate.setOnClickListener {
            val weather = getSelectedWeather()
            viewModel.setWeather(weather)
            viewModel.simulatedGen.observe(viewLifecycleOwner) { gen ->
                val result = "%.2f".format(gen)
                binding.etGenerated.setText(result)
                binding.tvSimulatedResult.text =
                    "📊 Simulated: $result kWh for $weather conditions"
            }
        }

        binding.btnSave.setOnClickListener {
            saveLog()
        }
    }

    private fun getSelectedWeather(): String = when {
        binding.rbSunny.isChecked  -> "Sunny"
        binding.rbPartly.isChecked -> "Partly Cloudy"
        binding.rbCloudy.isChecked -> "Cloudy"
        binding.rbRainy.isChecked  -> "Rainy"
        else -> "Sunny"
    }

    private fun saveLog() {
        val generatedStr = binding.etGenerated.text.toString()
        val consumedStr = binding.etConsumed.text.toString()
        val rateStr = binding.etRate.text.toString()

        if (generatedStr.isEmpty() || consumedStr.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please fill generated & consumed values",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val generated = generatedStr.toFloatOrNull()
        val consumed = consumedStr.toFloatOrNull()
        val rate = rateStr.toFloatOrNull() ?: 8.0f

        if (generated == null || consumed == null || generated < 0 || consumed < 0) {
            Toast.makeText(
                requireContext(),
                "Please enter valid positive numbers",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val battery = binding.seekbarBattery.progress.toFloat()
        val weather = getSelectedWeather()

        viewModel.saveEnergyLog(generated, consumed, battery, weather, rate)

        Toast.makeText(requireContext(), "✅ Energy log saved!", Toast.LENGTH_SHORT).show()

        findNavController().navigate(R.id.dashboardFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}