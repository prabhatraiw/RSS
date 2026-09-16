package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.DateHelper
import com.example.model.PravasItem
import com.example.ui.components.AddKaryakartaDialog
import com.example.ui.components.CalendarScheduleView
import com.example.ui.components.ClearConfirmDialog
import com.example.ui.components.EmptyScheduleView
import com.example.ui.components.ExportJsonDialog
import com.example.ui.components.FilterTabBar
import com.example.ui.components.ImportJsonDialog
import com.example.ui.components.KaryakartaCard
import com.example.ui.components.MatrixScheduleView
import com.example.ui.components.PeriodCard
import com.example.ui.components.PravasHeader
import com.example.ui.components.RulesCard
import com.example.ui.components.SavedPlansScreen
import com.example.ui.components.ShakhaCard
import com.example.ui.components.StatisticsBar
import com.example.ui.components.TableScheduleView
import com.example.ui.components.WeeklyDateScheduleView
import com.example.ui.components.WeeklyNameScheduleView
import com.example.ui.theme.BackgroundCream
import com.example.ui.theme.NavyDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.ImageExporter

enum class AppNavSection(
    val label: String,
    val icon: ImageVector,
    val description: String
) {
    SCHEDULE("शेड्यूल", Icons.Default.DateRange, "योजना एवं आंकड़े"),
    KARYAKARTAS("कार्यकर्ता, शाखा व नगर", Icons.Default.Person, "कार्यकर्ता, शाखा व नगर"),
    RULES("नियम व अवधि", Icons.Default.Settings, "नियम एवं दिनांक"),
    SAVED("Save", Icons.Default.Folder, "लोकल डेटाबेस (Room)")
}

@Composable
fun PravasApp(
    viewModel: PravasViewModel = viewModel()
) {
    val context = LocalContext.current

    val karyakartas by viewModel.karyakartas.collectAsState()
    val shakhas by viewModel.shakhas.collectAsState()
    val district by viewModel.district.collectAsState()
    val nagar by viewModel.nagar.collectAsState()
    val rules by viewModel.rules.collectAsState()
    val period by viewModel.period.collectAsState()
    val schedule by viewModel.filteredSchedule.collectAsState()
    val allSchedule by viewModel.schedule.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val filterShreni by viewModel.filterShreni.collectAsState()
    val filterShakha by viewModel.filterShakha.collectAsState()
    val filterKaryakarta by viewModel.filterKaryakarta.collectAsState()
    val expandedDaysIds by viewModel.expandedKaryakartaDays.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val totalDays by viewModel.totalDays.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val weekGroups by viewModel.weekGroups.collectAsState()
    val savedPlans by viewModel.savedPlans.collectAsState()

    var currentSection by remember { mutableStateOf(AppNavSection.SCHEDULE) }

    var showAddKaryakarta by remember { mutableStateOf(false) }
    var showImportJson by remember { mutableStateOf(false) }
    var showExportJson by remember { mutableStateOf(false) }
    var showClearConfirm by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            PravasHeader(toastMessage = toastMessage)
        },
        bottomBar = {
            // Material 3 NavigationBar on mobile screens
            BoxWithConstraints {
                if (maxWidth < 720.dp) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 6.dp
                    ) {
                        AppNavSection.entries.forEach { section ->
                            val isSelected = currentSection == section
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentSection = section },
                                icon = {
                                    Icon(
                                        imageVector = section.icon,
                                        contentDescription = section.label,
                                        tint = if (isSelected) SaffronPrimary else TextSecondary
                                    )
                                },
                                label = {
                                    Text(
                                        text = section.label,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) SaffronPrimary else TextSecondary
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color(0xFFFFF7ED)
                                )
                            )
                        }
                    }
                }
            }
        },
        containerColor = BackgroundCream
    ) { innerPadding ->

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isTablet = maxWidth >= 720.dp

            Row(modifier = Modifier.fillMaxSize()) {
                // NavigationRail on wide / tablet screens
                if (isTablet) {
                    NavigationRail(
                        containerColor = Color.White,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        AppNavSection.entries.forEach { section ->
                            val isSelected = currentSection == section
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = { currentSection = section },
                                icon = {
                                    Icon(
                                        imageVector = section.icon,
                                        contentDescription = section.label,
                                        tint = if (isSelected) SaffronPrimary else TextSecondary
                                    )
                                },
                                label = {
                                    Text(
                                        text = section.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationRailItemDefaults.colors(
                                    indicatorColor = Color(0xFFFFF7ED)
                                ),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                // Main Content View
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Crossfade(targetState = currentSection, label = "ScreenTransition") { section ->
                        when (section) {
                            AppNavSection.SCHEDULE -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Quick action banner to generate/refresh plan
                                    Button(
                                        onClick = { viewModel.generatePlan() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                                    ) {
                                        Text(
                                            text = "⚡ योजना बनाएँ / पुनः बनाएँ (Generate)",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    StatisticsBar(stats = stats)

                                    FilterTabBar(
                                        selectedTab = selectedTab,
                                        onTabSelected = { viewModel.setTab(it) },
                                        filterShreni = filterShreni,
                                        onFilterShreniChanged = { viewModel.setFilterShreni(it) },
                                        filterShakha = filterShakha,
                                        onFilterShakhaChanged = { viewModel.setFilterShakha(it) },
                                        filterKaryakarta = filterKaryakarta,
                                        onFilterKaryakartaChanged = { viewModel.setFilterKaryakarta(it) },
                                        shakhas = shakhas,
                                        karyakartas = karyakartas,
                                        onExportCsv = {
                                            val csv = ImageExporter.generateCsv(schedule)
                                            ImageExporter.shareCsv(
                                                context,
                                                csv,
                                                "pravas_${period.start}_${period.end}",
                                                "Shakha Pravas Yojna CSV"
                                            )
                                        },
                                        onExportPosterPng = {
                                            val bmp = ImageExporter.renderPosterBitmap(
                                                schedule = schedule,
                                                period = period,
                                                district = district,
                                                nagar = nagar
                                            )
                                            ImageExporter.shareBitmap(
                                                context,
                                                bmp,
                                                "pravas_poster_${period.start}_${period.end}",
                                                "Shakha Pravas Poster 4:5"
                                            )
                                        },
                                        onSave = { viewModel.manualSave() }
                                    )

                                    ScheduleContentView(
                                        schedule = schedule,
                                        allSchedule = allSchedule,
                                        selectedTab = selectedTab,
                                        period = period,
                                        totalDays = totalDays,
                                        karyakartas = karyakartas,
                                        shakhas = shakhas,
                                        weekGroups = weekGroups,
                                        district = district,
                                        nagar = nagar
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            AppNavSection.KARYAKARTAS -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    KaryakartaCard(
                                        karyakartas = karyakartas,
                                        shakhas = shakhas,
                                        expandedDaysIds = expandedDaysIds,
                                        onUpdateKaryakarta = { viewModel.updateKaryakarta(it) },
                                        onRemoveKaryakarta = { viewModel.removeKaryakarta(it) },
                                        onToggleDay = { id, day -> viewModel.toggleKaryakartaDay(id, day) },
                                        onToggleDaysExpanded = { viewModel.toggleKaryakartaDaysExpanded(it) },
                                        onAddKaryakarta = { _, _, _ -> showAddKaryakarta = true }
                                    )

                                    ShakhaCard(
                                        district = district,
                                        nagar = nagar,
                                        shakhas = shakhas,
                                        onDistrictChanged = { viewModel.updateDistrict(it) },
                                        onNagarChanged = { viewModel.updateNagar(it) },
                                        onAddShakha = { viewModel.addShakha(it) },
                                        onUpdateShakha = { old, new -> viewModel.updateShakha(old, new) },
                                        onRemoveShakha = { viewModel.removeShakha(it) }
                                    )

                                    Button(
                                        onClick = {
                                            viewModel.generatePlan()
                                            currentSection = AppNavSection.SCHEDULE
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                                    ) {
                                        Text(
                                            text = "⚡ इस जानकारी से योजना बनाएँ",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            AppNavSection.RULES -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RulesCard(
                                        rules = rules,
                                        onRulesChanged = { viewModel.updateRules(it) },
                                        onReset = { viewModel.resetRules() }
                                    )

                                    PeriodCard(
                                        period = period,
                                        totalDays = totalDays,
                                        weeksCount = stats.weeksCount,
                                        onPeriodChanged = { viewModel.updatePeriod(it.start, it.end) },
                                        onPresetSelected = { viewModel.setPeriodPreset(it) }
                                    )

                                    Button(
                                        onClick = {
                                            viewModel.generatePlan()
                                            currentSection = AppNavSection.SCHEDULE
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                                    ) {
                                        Text(
                                            text = "⚡ इन नियमों से योजना बनाएँ",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            AppNavSection.SAVED -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    SavedPlansScreen(
                                        savedPlans = savedPlans,
                                        currentDistrict = district,
                                        currentNagar = nagar,
                                        currentStartDate = period.start,
                                        currentEndDate = period.end,
                                        currentVisitsCount = allSchedule.size,
                                        onSaveCurrentPlan = { title ->
                                            viewModel.saveNamedPlanToRoom(title)
                                        },
                                        onLoadPlan = { plan ->
                                            viewModel.loadSavedPlanFromRoom(plan)
                                            currentSection = AppNavSection.SCHEDULE
                                        },
                                        onDeletePlan = { id ->
                                            viewModel.deleteSavedPlanFromRoom(id)
                                        },
                                        onLoadSavedPreferences = { viewModel.reloadSaved() },
                                        onExportJson = {
                                            exportedJsonText = viewModel.exportJsonString()
                                            showExportJson = true
                                        },
                                        onImportJson = { showImportJson = true },
                                        onClearAll = { showClearConfirm = true }
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showAddKaryakarta) {
        AddKaryakartaDialog(
            shakhas = shakhas,
            onDismiss = { showAddKaryakarta = false },
            onAdd = { name, shreni, kendriya ->
                viewModel.addKaryakarta(name, shreni, kendriya)
            }
        )
    }

    if (showImportJson) {
        ImportJsonDialog(
            onDismiss = { showImportJson = false },
            onImport = { json ->
                viewModel.importJsonString(json)
            }
        )
    }

    if (showExportJson) {
        ExportJsonDialog(
            jsonString = exportedJsonText,
            onDismiss = { showExportJson = false }
        )
    }

    if (showClearConfirm) {
        ClearConfirmDialog(
            onDismiss = { showClearConfirm = false },
            onConfirm = { viewModel.clearAllData() }
        )
    }
}

@Composable
private fun ScheduleContentView(
    schedule: List<PravasItem>,
    allSchedule: List<PravasItem>,
    selectedTab: String,
    period: com.example.model.Period,
    totalDays: Int,
    karyakartas: List<com.example.model.Karyakarta>,
    shakhas: List<String>,
    weekGroups: List<com.example.model.WeekGroup>,
    district: String,
    nagar: String
) {
    val context = LocalContext.current

    if (allSchedule.isEmpty()) {
        EmptyScheduleView(period = period, totalDays = totalDays)
    } else {
        when (selectedTab) {
            "calendar" -> CalendarScheduleView(items = schedule)
            "table" -> TableScheduleView(items = schedule)
            "matrix" -> MatrixScheduleView(
                items = schedule,
                karyakartas = karyakartas,
                shakhas = shakhas
            )
            "weekly" -> WeeklyDateScheduleView(
                weekGroups = weekGroups,
                karyakartas = karyakartas,
                onDownloadWeekPng = { week ->
                    val bmp = ImageExporter.renderWeeklyDateBitmap(
                        weekGroup = week,
                        district = district,
                        nagar = nagar
                    )
                    ImageExporter.shareBitmap(
                        context,
                        bmp,
                        "pravas_week_datewise_${week.index}_${DateHelper.formatIso(week.start)}",
                        "Weekly Date-wise Pravas PNG"
                    )
                }
            )
            "weeklyName" -> WeeklyNameScheduleView(
                weekGroups = weekGroups,
                onDownloadWeekNamePng = { week ->
                    val bmp = ImageExporter.renderWeeklyNameBitmap(
                        weekGroup = week,
                        district = district,
                        nagar = nagar
                    )
                    ImageExporter.shareBitmap(
                        context,
                        bmp,
                        "pravas_week_namewise_${week.index}_${DateHelper.formatIso(week.start)}",
                        "Weekly Name-wise Pravas PNG"
                    )
                }
            )
        }
    }
}
