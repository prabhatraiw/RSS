package com.example.data.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_plans")
data class SavedPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val district: String,
    val nagar: String,
    val startDate: String,
    val endDate: String,
    val totalVisits: Int,
    val scheduleJson: String,
    val karyakartasJson: String,
    val shakhasJson: String,
    val rulesJson: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey
    val key: String = "default_session",
    val district: String,
    val nagar: String,
    val startDate: String,
    val endDate: String,
    val selectedTab: String,
    val filterShreni: String,
    val filterShakha: String,
    val filterKaryakarta: String,
    val rulesJson: String,
    val karyakartasJson: String,
    val shakhasJson: String,
    val scheduleJson: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Dao
interface PravasDao {
    @Query("SELECT * FROM saved_plans ORDER BY timestamp DESC")
    fun getAllSavedPlans(): Flow<List<SavedPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: SavedPlanEntity): Long

    @Query("DELETE FROM saved_plans WHERE id = :id")
    suspend fun deletePlanById(id: Long)

    @Query("SELECT * FROM user_preferences WHERE `key` = :key LIMIT 1")
    suspend fun getUserPreferences(key: String = "default_session"): UserPreferencesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserPreferences(prefs: UserPreferencesEntity)

    @Query("DELETE FROM user_preferences WHERE `key` = :key")
    suspend fun clearUserPreferences(key: String = "default_session")
}

@Database(entities = [SavedPlanEntity::class, UserPreferencesEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pravasDao(): PravasDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shakha_pravas.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
