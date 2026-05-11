package com.suryashaktiapp.ui

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.suryashaktiapp.data.EnergyLog
import com.suryashaktiapp.databinding.FragmentReportBinding
import com.suryashaktiapp.viewmodel.EnergyViewModel
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EnergyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBarChart()
        setupRecyclerView()
        observeData()
    }

    private fun setupBarChart() {
        binding.barChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setDrawGridBackground(false)
            setBackgroundColor(Color.parseColor("#1A1A1A"))
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.parseColor("#9E9E9E")
                setDrawGridLines(false)
                granularity = 1f
            }
            axisLeft.apply {
                textColor = Color.parseColor("#9E9E9E")
                axisLineColor = Color.parseColor("#424242")
            }
            axisRight.isEnabled = false
        }
    }

    private fun setupRecyclerView() {
        binding.rvLogs.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeData() {
        viewModel.last30Logs.observe(viewLifecycleOwner) { logs ->
            updateBarChart(logs)
            binding.rvLogs.adapter = LogAdapter(logs)
        }

        viewModel.totalSavings.observe(viewLifecycleOwner) { savings ->
            binding.tvReportTotalSavings.text = "₹%.0f".format(savings ?: 0f)
        }

        viewModel.avgScore.observe(viewLifecycleOwner) { score ->
            binding.tvReportAvgScore.text = "%.0f/100".format(score ?: 0f)
        }
    }

    private fun updateBarChart(logs: List<EnergyLog>) {
        if (logs.isEmpty()) return
        val entries = logs.reversed().mapIndexed { index, log ->
            BarEntry(index.toFloat(), log.netSavingsRupees)
        }
        val labels = logs.reversed().map { it.date.takeLast(5) }
        val dataSet = BarDataSet(entries, "Savings").apply {
            color = Color.parseColor("#FFD600")
            valueTextColor = Color.parseColor("#FFFFFF")
            valueTextSize = 8f
        }
        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        binding.barChart.data = BarData(dataSet)
        binding.barChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class LogAdapter(private val logs: List<EnergyLog>) :
    RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val text: android.widget.TextView = view.findViewById(android.R.id.text1)
        val text2: android.widget.TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        view.setBackgroundColor(Color.parseColor("#1A1A1A"))
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val log = logs[position]
        holder.text.apply {
            text = "${log.date}  |  ${log.weatherCondition}  |  Score: ${log.independenceScore}"
            setTextColor(Color.parseColor("#FFD600"))
        }
        holder.text2.apply {
            text = "Gen: %.1f  Con: %.1f  Saved: ₹%.2f  Export: %.1f kWh"
                .format(
                    log.generatedKwh,
                    log.consumedKwh,
                    log.netSavingsRupees,
                    log.exportedKwh
                )
            setTextColor(Color.parseColor("#9E9E9E"))
        }
    }

    override fun getItemCount() = logs.size
}