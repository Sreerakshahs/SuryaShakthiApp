package com.suryashaktiapp.data


import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EnergyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: EnergyLog)

    @Query("SELECT * FROM energy_logs ORDER BY date DESC")
    fun getAllLogs(): LiveData<List<EnergyLog>>

    @Query("SELECT * FROM energy_logs ORDER BY date DESC LIMIT 30")
    fun getLast30DaysLogs(): LiveData<List<EnergyLog>>

    @Query("SELECT SUM(netSavingsRupees) FROM energy_logs")
    fun getTotalSavings(): LiveData<Float?>

    @Query("SELECT AVG(independenceScore) FROM energy_logs")
    fun getAverageIndependenceScore(): LiveData<Float?>
}