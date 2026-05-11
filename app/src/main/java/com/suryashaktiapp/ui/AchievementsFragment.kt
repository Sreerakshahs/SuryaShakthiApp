package com.suryashaktiapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.suryashaktiapp.databinding.FragmentAchievementsBinding
import com.suryashaktiapp.viewmodel.EnergyViewModel

class AchievementsFragment : Fragment() {

    private var _binding: FragmentAchievementsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EnergyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAchievementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    private fun observeData() {
        viewModel.allLogs.observe(viewLifecycleOwner) { logs ->

            // Total score = sum of all independence scores
            val totalScore = logs.sumOf { it.independenceScore }
            binding.tvTotalScore.text = totalScore.toString()

            // Badge 1 - First Log
            if (logs.isNotEmpty()) {
                binding.tvBadge1Status.text = "✅"
            }

            // Badge 2 - Save ₹100
            val totalSavings = logs.sumOf { it.netSavingsRupees.toDouble() }.toFloat()
            if (totalSavings >= 100f) {
                binding.tvBadge2Status.text = "✅"
            }

            // Badge 3 - Generate 50 kWh
            val totalGenerated = logs.sumOf { it.generatedKwh.toDouble() }.toFloat()
            if (totalGenerated >= 50f) {
                binding.tvBadge3Status.text = "✅"
            }

            // Badge 4 - Save ₹500
            if (totalSavings >= 500f) {
                binding.tvBadge4Status.text = "✅"
            }

            // Badge 5 - Save ₹1000
            if (totalSavings >= 1000f) {
                binding.tvBadge5Status.text = "✅"
            }

            // Badge 6 - Export to Grid
            val hasExported = logs.any { it.exportedKwh > 0 }
            if (hasExported) {
                binding.tvBadge6Status.text = "✅"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}