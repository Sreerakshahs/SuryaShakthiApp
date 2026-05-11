package com.suryashaktiapp.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EnergyLog::class], version = 1, exportSchema = false)
abstract class EnergyDatabase : RoomDatabase() {

    abstract fun energyDao(): EnergyDao

    companion object {
        @Volatile
        private var INSTANCE: EnergyDatabase? = null

        fun getDatabase(context: Context): EnergyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EnergyDatabase::class.java,
                    "surya_shakti_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}