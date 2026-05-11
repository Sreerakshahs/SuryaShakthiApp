package com.suryashaktiapp.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.suryashaktiapp.R
import com.suryashaktiapp.databinding.FragmentDashboardBinding
import com.suryashaktiapp.viewmodel.EnergyViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.suryashaktiapp.utils.NotificationHelper

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EnergyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDate()
        setupPieChart()
        observeData()

        binding.btnAchievements.setOnClickListener {
            findNavController().navigate(R.id.achievementsFragment)
        }

        binding.btnAbout.setOnClickListener {
            findNavController().navigate(R.id.aboutFragment)
        }
        binding.btnPmSurya.setOnClickListener {
            findNavController().navigate(R.id.pmSuryaGharFragment)
        }
    }

    private fun setupDate() {
        val format = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
        binding.tvDate.text = format.format(Date())
    }

    private fun setupPieChart() {
        binding.pieChartScore.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 60f
            setHoleColor(Color.parseColor("#242424"))
            transparentCircleRadius = 63f
            legend.isEnabled = false
            setTouchEnabled(false)
            setDrawEntryLabels(false)
        }
    }

    private fun updatePieChart(score: Int) {
        val entries = listOf(
            PieEntry(score.toFloat(), "Green"),
            PieEntry((100 - score).toFloat(), "Grid")
        )
        val dataSet = PieDataSet(entries, "Score").apply {
            colors = listOf(
                Color.parseColor("#00E676"),
                Color.parseColor("#424242")
            )
            setDrawValues(false)
        }
        binding.pieChartScore.data = PieData(dataSet)
        binding.pieChartScore.invalidate()
        binding.tvScoreLabel.text = "Score: $score / 100"

        val color = when {
            score >= 75 -> "#00E676"
            score >= 50 -> "#FFD600"
            else -> "#FF6D00"
        }
        binding.tvScoreLabel.setTextColor(Color.parseColor(color))
    }

    private fun observeData() {
        viewModel.allLogs.observe(viewLifecycleOwner) { logs ->
            if (logs.isNotEmpty()) {
                val latest = logs.first()
                binding.tvGenerated.text = "%.2f kWh".format(latest.generatedKwh)
                binding.tvConsumed.text = "%.2f kWh".format(latest.consumedKwh)
                binding.tvSavings.text = "₹%.2f".format(latest.netSavingsRupees)
                binding.tvBattery.text = "%.0f%%".format(latest.batteryLevel)
                updatePieChart(latest.independenceScore)

                // ── Notifications ──────────────────────────────
                // Battery low alert
                if (latest.batteryLevel < 20f) {
                    NotificationHelper.showBatteryLowAlert(
                        requireContext(), latest.batteryLevel
                    )
                }

                // Over generation alert
                if (latest.exportedKwh > 0) {
                    binding.cardExport.visibility = View.VISIBLE
                    binding.tvExport.text =
                        "You exported ${"%.2f".format(latest.exportedKwh)} kWh to grid! " +
                                "Est. credit: ₹${"%.2f".format(latest.exportedKwh * 3.5f)}"
                    NotificationHelper.showOverGenerationAlert(
                        requireContext(), latest.exportedKwh
                    )
                } else {
                    binding.cardExport.visibility = View.GONE
                }

                // High usage alert
                if (latest.consumedKwh > latest.generatedKwh) {
                    NotificationHelper.showHighUsageAlert(
                        requireContext(),
                        latest.consumedKwh,
                        latest.generatedKwh
                    )
                }

            } else {
                updatePieChart(0)
            }
            binding.tvSuggestion.text = viewModel.getPeakSuggestion()

            // Peak sun hours notification
            val hour = java.util.Calendar.getInstance()
                .get(java.util.Calendar.HOUR_OF_DAY)
            if (hour in 10..15) {
                NotificationHelper.showPeakSunAlert(requireContext())
            }
        }

        viewModel.totalSavings.observe(viewLifecycleOwner) { total ->
            binding.tvTotalSavings.text = "₹%.2f".format(total ?: 0f)
        }
    }
}
