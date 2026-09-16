package com.example.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val DEFAULT_SHRENIS = listOf("Sangathan Shreni", "Jagran Shreni", "Gatividhi")
val HINDI_DAY_INITIALS = listOf("र", "सो", "मं", "बु", "गु", "शु", "श")
val ENG_DAY_SHORT = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
val HINDI_DAY_FULL = listOf("रविवार", "सोमवार", "मंगलवार", "बुधवार", "गुरुवार", "शुक्रवार", "शनिवार")

data class Karyakarta(
    val id: Int,
    val name: String,
    val shreni: String,
    val kendriyaShakha: String,
    val availableDays: List<Int> = listOf(1, 2, 3, 4, 5, 6)
)

data class Rules(
    val maxPerDay: Int = 1,
    val maxPerWeek: Int = 2,
    val maxPerShakhaPerDay: Int = 2,
    val mustVisitAll: Boolean = true,
    val avoidDuplicate: Boolean = true,
    val excludeSunday: Boolean = true,
    val globalAvailableDays: List<Int> = listOf(1, 2, 3, 4, 5, 6),
    val weekStart: Int = 1 // 0 = Sunday, 1 = Monday
)

data class Period(
    val start: String, // "yyyy-MM-dd"
    val end: String    // "yyyy-MM-dd"
)

data class PravasItem(
    val id: String,
    val date: String, // "yyyy-MM-dd"
    val karyakartaId: Int,
    val karyakartaName: String,
    val shreni: String,
    val shakha: String,
    val kendriya: String
) {
    val dateObj: Date
        get() = DateHelper.parseIso(date) ?: Date()
}

data class WeekGroup(
    val index: Int,
    val start: Date,
    val end: Date,
    val items: List<PravasItem>
)

data class AppData(
    val karyakartas: List<Karyakarta>,
    val shakhas: List<String>,
    val district: String = "",
    val nagar: String = "",
    val rules: Rules,
    val period: Period,
    val schedule: List<PravasItem>
)

object DateHelper {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private val displayFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)

    fun formatIso(date: Date): String = isoFormat.format(date)
    fun parseIso(str: String): Date? = try {
        isoFormat.parse(str)
    } catch (_: Exception) {
        null
    }

    fun formatDisplay(date: Date): String = displayFormat.format(date)
    fun formatDisplay(isoStr: String): String {
        val d = parseIso(isoStr) ?: return isoStr
        return formatDisplay(d)
    }

    fun formatWithDay(date: Date): String {
        val cal = Calendar.getInstance().apply { time = date }
        val dayIndex = cal.get(Calendar.DAY_OF_WEEK) - 1
        val engDay = if (dayIndex in ENG_DAY_SHORT.indices) ENG_DAY_SHORT[dayIndex] else ""
        return "${formatDisplay(date)} ($engDay)"
    }

    fun formatWithDay(isoStr: String): String {
        val d = parseIso(isoStr) ?: return isoStr
        return formatWithDay(d)
    }

    fun getHindiDayName(date: Date): String {
        val cal = Calendar.getInstance().apply { time = date }
        val dayIndex = cal.get(Calendar.DAY_OF_WEEK) - 1
        return if (dayIndex in HINDI_DAY_FULL.indices) HINDI_DAY_FULL[dayIndex] else ""
    }

    fun addDays(date: Date, days: Int): Date {
        val cal = Calendar.getInstance().apply {
            time = date
            add(Calendar.DAY_OF_MONTH, days)
        }
        return cal.time
    }

    fun daysBetween(start: Date, end: Date): Int {
        val diffMs = end.time - start.time
        return (diffMs / (1000 * 60 * 60 * 24)).toInt() + 1
    }

    fun defaultPeriod(): Period {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        
        val startCal = Calendar.getInstance().apply {
            set(year, month, 15, 0, 0, 0)
        }
        val endCal = Calendar.getInstance().apply {
            set(year, month + 1, 14, 0, 0, 0)
        }
        return Period(
            start = formatIso(startCal.time),
            end = formatIso(endCal.time)
        )
    }
}
