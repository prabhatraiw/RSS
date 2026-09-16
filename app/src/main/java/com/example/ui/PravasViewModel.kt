package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PravasRepository
import com.example.data.RoomPravasRepository
import com.example.data.db.SavedPlanEntity
import com.example.logic.PravasGenerator
import com.example.model.AppData
import com.example.model.DateHelper
import com.example.model.Karyakarta
import com.example.model.Period
import com.example.model.PravasItem
import com.example.model.Rules
import com.example.model.WeekGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

data class PravasStats(
    val total: Int,
    val avgPerKaryakarta: String,
    val coveragePercent: Int,
    val weeksCount: Int
)

class PravasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PravasRepository(application)
    private val roomRepository = RoomPravasRepository(application)

    val savedPlans: StateFlow<List<SavedPlanEntity>> = roomRepository.savedPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _karyakartas = MutableStateFlow<List<Karyakarta>>(emptyList())
    val karyakartas: StateFlow<List<Karyakarta>> = _karyakartas.asStateFlow()

    private val _shakhas = MutableStateFlow<List<String>>(emptyList())
    val shakhas: StateFlow<List<String>> = _shakhas.asStateFlow()

    private val _district = MutableStateFlow("")
    val district: StateFlow<String> = _district.asStateFlow()

    private val _nagar = MutableStateFlow("")
    val nagar: StateFlow<String> = _nagar.asStateFlow()

    private val _rules = MutableStateFlow(Rules())
    val rules: StateFlow<Rules> = _rules.asStateFlow()

    private val _period = MutableStateFlow(DateHelper.defaultPeriod())
    val period: StateFlow<Period> = _period.asStateFlow()

    private val _schedule = MutableStateFlow<List<PravasItem>>(emptyList())
    val schedule: StateFlow<List<PravasItem>> = _schedule.asStateFlow()

    private val _selectedTab = MutableStateFlow("calendar")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    private val _filterShreni = MutableStateFlow("all")
    val filterShreni: StateFlow<String> = _filterShreni.asStateFlow()

    private val _filterShakha = MutableStateFlow("all")
    val filterShakha: StateFlow<String> = _filterShakha.asStateFlow()

    private val _filterKaryakarta = MutableStateFlow("all")
    val filterKaryakarta: StateFlow<String> = _filterKaryakarta.asStateFlow()

    private val _expandedKaryakartaDays = MutableStateFlow<Set<Int>>(emptySet())
    val expandedKaryakartaDays: StateFlow<Set<Int>> = _expandedKaryakartaDays.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        loadData()
        loadRoomSession()
    }

    private fun loadData() {
        val data = repository.loadData()
        _karyakartas.value = data.karyakartas
        _shakhas.value = data.shakhas
        _district.value = data.district
        _nagar.value = data.nagar
        _rules.value = data.rules
        _period.value = data.period
        _schedule.value = data.schedule
    }

    private fun loadRoomSession() {
        viewModelScope.launch {
            val session = roomRepository.loadSession()
            if (session != null) {
                if (session.karyakartas.isNotEmpty()) _karyakartas.value = session.karyakartas
                if (session.shakhas.isNotEmpty()) _shakhas.value = session.shakhas
                _district.value = session.district
                _nagar.value = session.nagar
                _rules.value = session.rules
                _period.value = session.period
                _selectedTab.value = session.selectedTab
                _filterShreni.value = session.filterShreni
                _filterShakha.value = session.filterShakha
                _filterKaryakarta.value = session.filterKaryakarta
                _schedule.value = session.schedule
            }
        }
    }

    val filteredSchedule: StateFlow<List<PravasItem>> = combine(
        _schedule,
        _filterShreni,
        _filterShakha,
        _filterKaryakarta
    ) { items, shreni, shakha, karyakartaId ->
        items.filter { item ->
            (shreni == "all" || item.shreni == shreni) &&
            (shakha == "all" || item.shakha == shakha) &&
            (karyakartaId == "all" || item.karyakartaId.toString() == karyakartaId)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalDays: StateFlow<Int> = _period.combine(_schedule) { p, _ ->
        val s = DateHelper.parseIso(p.start) ?: Date()
        val e = DateHelper.parseIso(p.end) ?: Date()
        DateHelper.daysBetween(s, e).coerceAtLeast(1)
    }.stateIn(viewModelScope, SharingStarted.Lazily, 30)

    val stats: StateFlow<PravasStats> = combine(
        filteredSchedule,
        _karyakartas,
        _shakhas,
        _period,
        _rules
    ) { filtered, kList, sList, p, r ->
        val total = filtered.size
        val avg = if (kList.isNotEmpty()) String.format("%.1f", total.toDouble() / kList.size) else "0.0"

        val s = DateHelper.parseIso(p.start) ?: Date()
        val e = DateHelper.parseIso(p.end) ?: Date()
        val totalD = DateHelper.daysBetween(s, e).coerceAtLeast(1)
        val weeks = PravasGenerator.splitIntoWeeks(s, e, r.weekStart).size

        val uniquePairs = filtered.map { "${it.date}|${it.shakha}" }.toSet().size
        val maxPotential = sList.size * totalD
        val coverage = if (maxPotential > 0) {
            ((uniquePairs.toDouble() / maxPotential) * 100).toInt().coerceIn(0, 100)
        } else 0

        PravasStats(
            total = total,
            avgPerKaryakarta = avg,
            coveragePercent = coverage,
            weeksCount = weeks
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, PravasStats(0, "0.0", 0, 0))

    val weekGroups: StateFlow<List<WeekGroup>> = combine(
        filteredSchedule,
        _period,
        _rules
    ) { items, p, r ->
        val s = DateHelper.parseIso(p.start) ?: Date()
        val e = DateHelper.parseIso(p.end) ?: Date()
        PravasGenerator.groupIntoWeeks(items, s, e, r.weekStart)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun generatePlan() {
        val s = DateHelper.parseIso(_period.value.start) ?: Date()
        val e = DateHelper.parseIso(_period.value.end) ?: Date()

        if (s.after(e)) {
            showToast("कृपया वैध तिथि सीमा चुनें")
            return
        }

        val generated = PravasGenerator.generateSchedule(
            karyakartas = _karyakartas.value,
            shakhas = _shakhas.value,
            rules = _rules.value,
            startDate = s,
            endDate = e
        )
        _schedule.value = generated
        saveCurrentState()
        showToast("योजना सफलतापूर्वक तैयार की गई (${generated.size} प्रवास)")
    }

    fun saveCurrentState() {
        val data = AppData(
            karyakartas = _karyakartas.value,
            shakhas = _shakhas.value,
            district = _district.value,
            nagar = _nagar.value,
            rules = _rules.value,
            period = _period.value,
            schedule = _schedule.value
        )
        repository.saveData(data)

        viewModelScope.launch {
            roomRepository.saveSession(
                district = _district.value,
                nagar = _nagar.value,
                period = _period.value,
                selectedTab = _selectedTab.value,
                filterShreni = _filterShreni.value,
                filterShakha = _filterShakha.value,
                filterKaryakarta = _filterKaryakarta.value,
                rules = _rules.value,
                karyakartas = _karyakartas.value,
                shakhas = _shakhas.value,
                schedule = _schedule.value
            )
        }
    }

    fun saveNamedPlanToRoom(title: String) {
        viewModelScope.launch {
            val planTitle = title.ifBlank { "प्रवास योजना - ${_period.value.start}" }
            roomRepository.saveNamedPlan(
                title = planTitle,
                district = _district.value,
                nagar = _nagar.value,
                period = _period.value,
                rules = _rules.value,
                karyakartas = _karyakartas.value,
                shakhas = _shakhas.value,
                schedule = _schedule.value
            )
            showToast("योजना रूम डेटाबेस में सहेजी गई: $planTitle")
        }
    }

    fun loadSavedPlanFromRoom(plan: SavedPlanEntity) {
        val parsed = roomRepository.parsePlanEntity(plan)
        if (parsed != null) {
            _district.value = parsed.district
            _nagar.value = parsed.nagar
            _period.value = parsed.period
            _rules.value = parsed.rules
            _karyakartas.value = parsed.karyakartas
            _shakhas.value = parsed.shakhas
            _schedule.value = parsed.schedule
            saveCurrentState()
            showToast("सहेजी गई योजना लोड की गई: ${plan.title}")
        }
    }

    fun deleteSavedPlanFromRoom(id: Long) {
        viewModelScope.launch {
            roomRepository.deletePlan(id)
            showToast("योजना हटाई गई")
        }
    }

    fun manualSave() {
        saveCurrentState()
        showToast("डेटा सुरक्षित रूप से सहेजा गया!")
    }

    fun reloadSaved() {
        loadData()
        loadRoomSession()
        showToast("सहेजा गया डेटा लोड किया गया")
    }

    fun clearAllData() {
        repository.clearData()
        viewModelScope.launch {
            roomRepository.clearSession()
        }
        val default = repository.defaultData()
        _karyakartas.value = default.karyakartas
        _shakhas.value = default.shakhas
        _district.value = ""
        _nagar.value = ""
        _rules.value = default.rules
        _period.value = default.period
        _schedule.value = emptyList()
        showToast("सभी सहेजा गया डेटा रीसेट किया गया")
    }

    fun exportJsonString(): String {
        val data = AppData(
            karyakartas = _karyakartas.value,
            shakhas = _shakhas.value,
            district = _district.value,
            nagar = _nagar.value,
            rules = _rules.value,
            period = _period.value,
            schedule = _schedule.value
        )
        return repository.toJson(data)
    }

    fun importJsonString(json: String): Boolean {
        return try {
            val data = repository.parseJson(json)
            _karyakartas.value = data.karyakartas
            _shakhas.value = data.shakhas
            _district.value = data.district
            _nagar.value = data.nagar
            _rules.value = data.rules
            _period.value = data.period
            _schedule.value = data.schedule
            saveCurrentState()
            showToast("JSON डेटा सफलतापूर्वक आयात किया गया")
            true
        } catch (_: Exception) {
            showToast("अमान्य JSON डेटा")
            false
        }
    }

    fun setTab(tab: String) {
        _selectedTab.value = tab
    }

    fun setFilterShreni(shreni: String) {
        _filterShreni.value = shreni
    }

    fun setFilterShakha(shakha: String) {
        _filterShakha.value = shakha
    }

    fun setFilterKaryakarta(id: String) {
        _filterKaryakarta.value = id
    }

    fun updateDistrict(d: String) {
        _district.value = d
        saveCurrentState()
    }

    fun updateNagar(n: String) {
        _nagar.value = n
        saveCurrentState()
    }

    fun updateRules(newRules: Rules) {
        _rules.value = newRules
        saveCurrentState()
    }

    fun resetRules() {
        _rules.value = Rules()
        saveCurrentState()
        showToast("नियम रीसेट किए गए")
    }

    fun toggleGlobalDay(day: Int) {
        val current = _rules.value.globalAvailableDays.toMutableSet()
        if (current.contains(day)) current.remove(day) else current.add(day)
        _rules.value = _rules.value.copy(globalAvailableDays = current.sorted())
        saveCurrentState()
    }

    fun updatePeriod(start: String, end: String) {
        _period.value = Period(start, end)
        saveCurrentState()
    }

    fun setPeriodPreset(days: Int) {
        val s = DateHelper.parseIso(_period.value.start) ?: Date()
        val e = DateHelper.addDays(s, days - 1)
        _period.value = Period(
            start = DateHelper.formatIso(s),
            end = DateHelper.formatIso(e)
        )
        saveCurrentState()
    }

    fun updateKaryakarta(updated: Karyakarta) {
        _karyakartas.value = _karyakartas.value.map {
            if (it.id == updated.id) updated else it
        }
        // Also cascade name or kendriya if needed in schedule
        _schedule.value = _schedule.value.map { item ->
            if (item.karyakartaId == updated.id) {
                item.copy(
                    karyakartaName = updated.name,
                    shreni = updated.shreni,
                    kendriya = updated.kendriyaShakha
                )
            } else item
        }
        saveCurrentState()
    }

    fun addKaryakarta(name: String, shreni: String, kendriyaShakha: String) {
        val maxId = _karyakartas.value.maxOfOrNull { it.id } ?: 0
        val newK = Karyakarta(
            id = maxId + 1,
            name = name.ifBlank { "नया कार्यकर्ता" },
            shreni = shreni,
            kendriyaShakha = kendriyaShakha.ifBlank { _shakhas.value.firstOrNull() ?: "" },
            availableDays = listOf(1, 2, 3, 4, 5, 6)
        )
        _karyakartas.value = _karyakartas.value + newK
        saveCurrentState()
        showToast("कार्यकर्ता जोड़ा गया")
    }

    fun removeKaryakarta(id: Int) {
        _karyakartas.value = _karyakartas.value.filter { it.id != id }
        _schedule.value = _schedule.value.filter { it.karyakartaId != id }
        saveCurrentState()
    }

    fun toggleKaryakartaDay(karyakartaId: Int, day: Int) {
        _karyakartas.value = _karyakartas.value.map { k ->
            if (k.id == karyakartaId) {
                val current = k.availableDays.toMutableSet()
                if (current.contains(day)) current.remove(day) else current.add(day)
                k.copy(availableDays = current.sorted())
            } else k
        }
        saveCurrentState()
    }

    fun toggleKaryakartaDaysExpanded(id: Int) {
        val current = _expandedKaryakartaDays.value.toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        _expandedKaryakartaDays.value = current
    }

    fun addShakha(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank() && !_shakhas.value.contains(trimmed)) {
            _shakhas.value = _shakhas.value + trimmed
            saveCurrentState()
            showToast("शाखा जोड़ी गई: $trimmed")
        }
    }

    fun updateShakha(oldName: String, newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isBlank() || trimmed == oldName) return

        _shakhas.value = _shakhas.value.map { if (it == oldName) trimmed else it }
        // Cascade to karyakarta kendriyaShakha
        _karyakartas.value = _karyakartas.value.map { k ->
            if (k.kendriyaShakha == oldName) k.copy(kendriyaShakha = trimmed) else k
        }
        // Cascade to schedule
        _schedule.value = _schedule.value.map { item ->
            if (item.shakha == oldName) item.copy(shakha = trimmed)
            else if (item.kendriya == oldName) item.copy(kendriya = trimmed)
            else item
        }
        saveCurrentState()
    }

    fun removeShakha(name: String) {
        _shakhas.value = _shakhas.value.filter { it != name }
        // Remove related assignments from schedule
        _schedule.value = _schedule.value.filter { it.shakha != name }
        saveCurrentState()
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
