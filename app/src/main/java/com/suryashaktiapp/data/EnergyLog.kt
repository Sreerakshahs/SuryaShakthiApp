package com.suryashaktiapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "energy_logs")
data class EnergyLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val generatedKwh: Float,
    val consumedKwh: Float,
    val batteryLevel: Float,
    val weatherCondition: String,
    val ratePerUnit: Float = 8.0f,
    val netSavingsRupees: Float,
    val exportedKwh: Float,
    val independenceScore: Int
)