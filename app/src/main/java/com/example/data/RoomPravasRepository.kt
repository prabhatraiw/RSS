package com.example.data

import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.db.PravasDao
import com.example.data.db.SavedPlanEntity
import com.example.data.db.UserPreferencesEntity
import com.example.model.Karyakarta
import com.example.model.Period
import com.example.model.PravasItem
import com.example.model.Rules
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class RoomPravasRepository(context: Context) {

    private val dao: PravasDao = AppDatabase.getInstance(context).pravasDao()
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val karyakartaListType = Types.newParameterizedType(List::class.java, Karyakarta::class.java)
    private val stringListType = Types.newParameterizedType(List::class.java, String::class.java)
    private val scheduleListType = Types.newParameterizedType(List::class.java, PravasItem::class.java)

    private val karyakartaAdapter = moshi.adapter<List<Karyakarta>>(karyakartaListType)
    private val stringListAdapter = moshi.adapter<List<String>>(stringListType)
    private val rulesAdapter = moshi.adapter(Rules::class.java)
    private val scheduleAdapter = moshi.adapter<List<PravasItem>>(scheduleListType)

    val savedPlans: Flow<List<SavedPlanEntity>> = dao.getAllSavedPlans()

    suspend fun saveSession(
        district: String,
        nagar: String,
        period: Period,
        selectedTab: String,
        filterShreni: String,
        filterShakha: String,
        filterKaryakarta: String,
        rules: Rules,
        karyakartas: List<Karyakarta>,
        shakhas: List<String>,
        schedule: List<PravasItem>
    ) = withContext(Dispatchers.IO) {
        val entity = UserPreferencesEntity(
            key = "default_session",
            district = district,
            nagar = nagar,
            startDate = period.start,
            endDate = period.end,
            selectedTab = selectedTab,
            filterShreni = filterShreni,
            filterShakha = filterShakha,
            filterKaryakarta = filterKaryakarta,
            rulesJson = rulesAdapter.toJson(rules),
            karyakartasJson = karyakartaAdapter.toJson(karyakartas),
            shakhasJson = stringListAdapter.toJson(shakhas),
            scheduleJson = scheduleAdapter.toJson(schedule)
        )
        dao.saveUserPreferences(entity)
    }

    suspend fun loadSession(): SessionData? = withContext(Dispatchers.IO) {
        val entity = dao.getUserPreferences() ?: return@withContext null
        try {
            val rules = rulesAdapter.fromJson(entity.rulesJson) ?: Rules()
            val karyakartas = karyakartaAdapter.fromJson(entity.karyakartasJson) ?: emptyList()
            val shakhas = stringListAdapter.fromJson(entity.shakhasJson) ?: emptyList()
            val schedule = scheduleAdapter.fromJson(entity.scheduleJson) ?: emptyList()

            SessionData(
                district = entity.district,
                nagar = entity.nagar,
                period = Period(start = entity.startDate, end = entity.endDate),
                selectedTab = entity.selectedTab,
                filterShreni = entity.filterShreni,
                filterShakha = entity.filterShakha,
                filterKaryakarta = entity.filterKaryakarta,
                rules = rules,
                karyakartas = karyakartas,
                shakhas = shakhas,
                schedule = schedule
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveNamedPlan(
        title: String,
        district: String,
        nagar: String,
        period: Period,
        rules: Rules,
        karyakartas: List<Karyakarta>,
        shakhas: List<String>,
        schedule: List<PravasItem>
    ): Long = withContext(Dispatchers.IO) {
        val plan = SavedPlanEntity(
            title = title,
            district = district,
            nagar = nagar,
            startDate = period.start,
            endDate = period.end,
            totalVisits = schedule.size,
            rulesJson = rulesAdapter.toJson(rules),
            karyakartasJson = karyakartaAdapter.toJson(karyakartas),
            shakhasJson = stringListAdapter.toJson(shakhas),
            scheduleJson = scheduleAdapter.toJson(schedule)
        )
        dao.insertPlan(plan)
    }

    suspend fun deletePlan(id: Long) = withContext(Dispatchers.IO) {
        dao.deletePlanById(id)
    }

    suspend fun clearSession() = withContext(Dispatchers.IO) {
        dao.clearUserPreferences()
    }

    fun parsePlanEntity(entity: SavedPlanEntity): SessionData? {
        return try {
            val rules = rulesAdapter.fromJson(entity.rulesJson) ?: Rules()
            val karyakartas = karyakartaAdapter.fromJson(entity.karyakartasJson) ?: emptyList()
            val shakhas = stringListAdapter.fromJson(entity.shakhasJson) ?: emptyList()
            val schedule = scheduleAdapter.fromJson(entity.scheduleJson) ?: emptyList()

            SessionData(
                district = entity.district,
                nagar = entity.nagar,
                period = Period(start = entity.startDate, end = entity.endDate),
                selectedTab = "calendar",
                filterShreni = "all",
                filterShakha = "all",
                filterKaryakarta = "all",
                rules = rules,
                karyakartas = karyakartas,
                shakhas = shakhas,
                schedule = schedule
            )
        } catch (e: Exception) {
            null
        }
    }

    data class SessionData(
        val district: String,
        val nagar: String,
        val period: Period,
        val selectedTab: String,
        val filterShreni: String,
        val filterShakha: String,
        val filterKaryakarta: String,
        val rules: Rules,
        val karyakartas: List<Karyakarta>,
        val shakhas: List<String>,
        val schedule: List<PravasItem>
    )
}
