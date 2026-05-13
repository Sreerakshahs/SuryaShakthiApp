package com.suryashaktiapp.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.suryashaktiapp.databinding.FragmentAnalyticsBinding
import com.suryashaktiapp.viewmodel.EnergyViewModel

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EnergyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBarChart()
        observeData()
        setupROICalculator()
        setupAIChat()
        setupQuickChips()
    }

    private fun setupBarChart() {
        val chart = binding.barChartComparison
        chart.description.isEnabled = false
        chart.legend.isEnabled = true
        chart.setDrawGridBackground(false)
        chart.setBackgroundColor(Color.parseColor("#242424"))
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.textColor = Color.parseColor("#9E9E9E")
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.granularity = 1f
        chart.axisLeft.textColor = Color.parseColor("#9E9E9E")
        chart.axisRight.isEnabled = false
    }

    private fun observeData() {
        viewModel.allLogs.observe(viewLifecycleOwner) { logs ->
            if (logs.isEmpty()) {
                binding.tvCo2Saved.text = "0.00 kg"
                binding.tvTreesEquivalent.text = "🌳 Log energy data to see CO2 savings!"
                binding.tvEfficiencyGrade.text = "--"
                binding.tvEfficiencyDesc.text = "Log energy data to get your rating!"
                return@observe
            }

            // CO2 Savings
            val totalGenerated = logs.sumOf { it.generatedKwh.toDouble() }.toFloat()
            val co2Saved = totalGenerated * 0.82f
            val treesEquivalent = (co2Saved / 21).toInt()
            binding.tvCo2Saved.text = "%.2f kg".format(co2Saved)
            binding.tvTreesEquivalent.text =
                "🌳 Equivalent to $treesEquivalent trees planted"

            // Efficiency Grade
            val avgScore = logs.map { it.independenceScore }.average().toFloat()
            val grade: String
            val desc: String
            val gradeColor: String
            when {
                avgScore >= 85 -> {
                    grade = "A+"
                    desc = "Outstanding solar performance!"
                    gradeColor = "#00E676"
                }
                avgScore >= 70 -> {
                    grade = "A"
                    desc = "Excellent solar utilization!"
                    gradeColor = "#00C853"
                }
                avgScore >= 55 -> {
                    grade = "B"
                    desc = "Good solar performance!"
                    gradeColor = "#FFD600"
                }
                avgScore >= 40 -> {
                    grade = "C"
                    desc = "Average solar utilization."
                    gradeColor = "#FF6D00"
                }
                else -> {
                    grade = "D"
                    desc = "Needs improvement."
                    gradeColor = "#FF1744"
                }
            }
            binding.tvEfficiencyGrade.text = grade
            binding.tvEfficiencyGrade.setTextColor(Color.parseColor(gradeColor))
            binding.tvEfficiencyDesc.text = desc

            // Bar Chart
            val last7 = logs.take(7).reversed()
            val genEntries = last7.mapIndexed { i, log ->
                BarEntry(i.toFloat(), log.generatedKwh)
            }
            val conEntries = last7.mapIndexed { i, log ->
                BarEntry(i.toFloat(), log.consumedKwh)
            }
            val labels = last7.map { it.date.takeLast(5) }

            val genDataSet = BarDataSet(genEntries, "Generated ⚡")
            genDataSet.color = Color.parseColor("#FFD600")
            genDataSet.valueTextColor = Color.WHITE
            genDataSet.valueTextSize = 8f

            val conDataSet = BarDataSet(conEntries, "Consumed 🏠")
            conDataSet.color = Color.parseColor("#FF6D00")
            conDataSet.valueTextColor = Color.WHITE
            conDataSet.valueTextSize = 8f

            val barData = BarData(genDataSet, conDataSet)
            barData.barWidth = 0.35f

            binding.barChartComparison.xAxis.valueFormatter =
                IndexAxisValueFormatter(labels)
            binding.barChartComparison.data = barData
            binding.barChartComparison.groupBars(0f, 0.1f, 0.05f)
            binding.barChartComparison.invalidate()
        }
    }

    private fun setupROICalculator() {
        binding.btnCalculateRoi.setOnClickListener {
            val cost = binding.etInstallationCost.text.toString()
                .toFloatOrNull() ?: 150000f
            val monthlySavings = binding.etMonthlySavings.text.toString()
                .toFloatOrNull() ?: 1200f

            if (monthlySavings == 0f) return@setOnClickListener

            val paybackMonths = cost / monthlySavings
            val paybackYears = paybackMonths / 12
            val savings10yr = (monthlySavings * 120) - cost
            val savings25yr = (monthlySavings * 300) - cost

            binding.layoutRoiResults.visibility = View.VISIBLE
            binding.tvPaybackPeriod.text =
                "⏱️ Payback Period: ${"%.1f".format(paybackYears)} years"
            binding.tv10yrSavings.text =
                "💰 10-Year Net Savings: ₹${"%.0f".format(savings10yr)}"
            binding.tv25yrSavings.text =
                "🏆 25-Year Net Savings: ₹${"%.0f".format(savings25yr)}"
        }
    }

    private fun setupQuickChips() {
        binding.chipSave.setOnClickListener {
            processQuestion("save energy")
        }
        binding.chipBattery.setOnClickListener {
            processQuestion("battery tips")
        }
        binding.chipCo2.setOnClickListener {
            processQuestion("co2 savings")
        }
        binding.chipRoi.setOnClickListener {
            processQuestion("roi")
        }
        binding.chipPanel.setOnClickListener {
            processQuestion("panel care")
        }
    }

    private fun setupAIChat() {
        binding.btnSendChat.setOnClickListener {
            val question = binding.etChatInput.text.toString().trim()
            if (question.isEmpty()) return@setOnClickListener
            processQuestion(question)
            binding.etChatInput.text?.clear()
        }
    }

    private fun processQuestion(question: String) {
        val q = question.lowercase()

        // Show user message
        addChatMessage(question, isUser = true)

        val responses = mapOf(
            "save" to "💡 Run heavy appliances between 10AM-3PM during peak sun hours to maximize free solar energy!",
            "battery" to "🔋 Keep battery between 20-90% for longer life. Avoid full discharge and overcharging!",
            "score" to "⭐ Green Score = Solar Coverage (70%) + Battery Level (30%). Aim for 75+ for A grade!",
            "generation" to "⚡ A 2kW panel generates 8-14 kWh on sunny days and only 1-3 kWh on cloudy days.",
            "savings" to "💰 Your savings = Solar kWh × Rate per unit (₹). Check your 30-day report for total savings!",
            "panel" to "🌞 Clean panels every 2 weeks with soft cloth and water! Dust reduces efficiency by 25%.",
            "export" to "🔛 Over-generation means you are sending surplus power to the grid. You earn credit for this!",
            "weather" to "🌤️ Sunny days give maximum generation. Cloudy days reduce solar output by 60-70%.",
            "tip" to "💡 Switch to LED bulbs — they use 80% less energy. Replacing 10 saves 0.5 kWh daily!",
            "hello" to "👋 Hello! I can help with energy saving, battery, score, generation, panel care and more!",
            "hi" to "👋 Hi there! I am your solar AI assistant. What would you like to know?",
            "co2" to "🌍 Every kWh of solar energy saves 0.82 kg of CO2! You are helping the planet every day!",
            "roi" to "💰 Average solar ROI payback period in India is 5-7 years. After that electricity is FREE!",
            "pm" to "🌞 PM Surya Ghar Yojana gives up to ₹78,000 subsidy! Check the Govt tab for details.",
            "subsidy" to "🏦 Get up to ₹78,000 central subsidy under PM Surya Ghar Yojana for 3kW system!",
            "grid" to "🔛 When you export to grid you earn approximately ₹3.5 per kWh credit in most states!",
            "clean" to "🧹 Clean your solar panels with soft cloth and water in early morning before they heat up!",
            "led" to "💡 Replace all bulbs with LEDs! Replacing 10 bulbs saves 0.5 kWh per day — ₹4 daily!",
            "fan" to "🌀 Ceiling fan uses 75W, AC uses 1500W. Use fan in moderate weather and save big!",
            "charge" to "📱 Charge phones, tablets and laptops between 9AM-4PM using direct solar power!",
            "peak" to "☀️ Peak sun hours are 10AM to 3PM. Run washing machine, pump and iron during this time!",
            "inverter" to "⚡ Keep inverter ventilated and clean. Check battery water levels monthly for long life!",
            "india" to "🇮🇳 India gets 300+ sunny days per year! Your 2kW system can save ₹19,200 annually!",
            "night" to "🌙 At night, use stored battery power. Minimize usage to make battery last till morning!",
            "cost" to "💰 A 2kW solar system costs ₹1-1.5 lakh in India. With subsidy it comes down to ₹40,000!"
        )

        val response = responses.entries
            .firstOrNull { q.contains(it.key) }?.value
            ?: "🤖 I can help with: energy saving, battery, score, generation, " +
            "panel care, CO2, ROI, subsidies, weather and more! Try asking something specific."

        binding.root.postDelayed({
            if (_binding != null) {
                addChatMessage(response, isUser = false)
            }
        }, 600)
    }

    private fun addChatMessage(message: String, isUser: Boolean) {
        val tv = TextView(requireContext())
        tv.text = if (isUser) "👤 $message" else "🤖 $message"
        tv.textSize = 13f
        tv.setTextColor(Color.WHITE)
        tv.setPadding(20, 16, 20, 16)
        tv.setBackgroundColor(
            if (isUser) Color.parseColor("#1A3A1A")
            else Color.parseColor("#2A1A4E")
        )

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.bottomMargin = 12
        if (isUser) {
            params.gravity = Gravity.END
            params.marginStart = 60
        } else {
            params.gravity = Gravity.START
            params.marginEnd = 60
        }
        tv.layoutParams = params
        binding.chatContainer.addView(tv)

        // Auto scroll to bottom
        binding.scrollChat.post {
            binding.scrollChat.fullScroll(View.FOCUS_DOWN)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}