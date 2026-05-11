package com.suryashaktiapp.viewmodel



import android.app.Application
import androidx.lifecycle.*
import com.suryashaktiapp.data.EnergyDatabase
import com.suryashaktiapp.data.EnergyLog
import com.suryashaktiapp.utils.EnergyCalculator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EnergyViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = EnergyDatabase.getDatabase(application).energyDao()

    val allLogs: LiveData<List<EnergyLog>> = dao.getAllLogs()
    val last30Logs: LiveData<List<EnergyLog>> = dao.getLast30DaysLogs()
    val totalSavings: LiveData<Float?> = dao.getTotalSavings()
    val avgScore: LiveData<Float?> = dao.getAverageIndependenceScore()

    private val _selectedWeather = MutableLiveData("Sunny")
    val selectedWeather: LiveData<String> = _selectedWeather

    private val _simulatedGen = MutableLiveData(0f)
    val simulatedGen: LiveData<Float> = _simulatedGen

    fun setWeather(weather: String) {
        _selectedWeather.value = weather
        _simulatedGen.value = EnergyCalculator.simulateGeneration(weather)
    }

    fun saveEnergyLog(
        generated: Float,
        consumed: Float,
        battery: Float,
        weather: String,
        ratePerUnit: Float
    ) {
        viewModelScope.launch {
            val log = EnergyLog(
                date = getTodayDate(),
                generatedKwh = generated,
                consumedKwh = consumed,
                batteryLevel = battery,
                weatherCondition = weather,
                ratePerUnit = ratePerUnit,
                netSavingsRupees = EnergyCalculator.calculateSavings(
                    generated, consumed, ratePerUnit
                ),
                exportedKwh = EnergyCalculator.calculateExport(generated, consumed),
                independenceScore = EnergyCalculator.calculateIndependenceScore(
                    generated, consumed, battery
                )
            )
            dao.insertLog(log)
        }
    }

    fun getPeakSuggestion(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return EnergyCalculator.getPeakSuggestion(
            _selectedWeather.value ?: "Sunny", hour
        )
    }

    private fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}