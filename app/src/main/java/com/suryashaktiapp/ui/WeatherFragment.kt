package com.suryashaktiapp.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.suryashaktiapp.databinding.FragmentWeatherBinding
import com.suryashaktiapp.utils.EnergyCalculator

class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private var selectedWeather = "Sunny"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Default selection
        highlightCard("Sunny")
        runSimulation()

        // Weather card clicks
        binding.cardSunny.setOnClickListener {
            selectedWeather = "Sunny"
            highlightCard("Sunny")
        }
        binding.cardPartly.setOnClickListener {
            selectedWeather = "Partly Cloudy"
            highlightCard("Partly Cloudy")
        }
        binding.cardCloudy.setOnClickListener {
            selectedWeather = "Cloudy"
            highlightCard("Cloudy")
        }
        binding.cardRainy.setOnClickListener {
            selectedWeather = "Rainy"
            highlightCard("Rainy")
        }

        binding.btnRunSimulation.setOnClickListener {
            runSimulation()
        }
    }

    private fun highlightCard(weather: String) {
        val yellow = Color.parseColor("#1A2900")
        val dark = Color.parseColor("#242424")

        binding.cardSunny.setCardBackgroundColor(if (weather == "Sunny") yellow else dark)
        binding.cardPartly.setCardBackgroundColor(
            if (weather == "Partly Cloudy") yellow else dark)
        binding.cardCloudy.setCardBackgroundColor(if (weather == "Cloudy") yellow else dark)
        binding.cardRainy.setCardBackgroundColor(if (weather == "Rainy") yellow else dark)

        val emoji = when (weather) {
            "Sunny" -> "☀️"
            "Partly Cloudy" -> "⛅"
            "Cloudy" -> "☁️"
            "Rainy" -> "🌧️"
            else -> "☀️"
        }
        binding.tvWeatherSelected.text = "$emoji $weather Selected"
    }

    private fun runSimulation() {
        val generated = EnergyCalculator.simulateGeneration(selectedWeather)
        val monthlySavings = generated * 30 * 8f

        binding.tvEstimatedGen.text = "%.2f kWh".format(generated)
        binding.tvMonthlyEstimate.text = "₹%.0f".format(monthlySavings)

        val tip = when (selectedWeather) {
            "Sunny" ->
                "🌟 Excellent day! Run all heavy appliances now and charge your battery fully."
            "Partly Cloudy" ->
                "👍 Good generation. Prioritize essential appliances and moderate usage."
            "Cloudy" ->
                "⚠️ Limited solar today. Conserve energy and avoid heavy appliances."
            "Rainy" ->
                "🔋 Very low generation. Rely on stored battery and minimize consumption."
            else -> ""
        }
        binding.tvWeatherTip.text = tip
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}