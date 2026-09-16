package com.example.logic

import com.example.model.DateHelper
import com.example.model.Karyakarta
import com.example.model.PravasItem
import com.example.model.Rules
import com.example.model.WeekGroup
import java.util.Calendar
import java.util.Date
import java.util.UUID

object PravasGenerator {

    /**
     * Splits date range into weeks based on weekStart (0 = Sunday, 1 = Monday).
     */
    fun splitIntoWeeks(start: Date, end: Date, weekStart: Int): List<List<Date>> {
        val result = mutableListOf<List<Date>>()
        
        // Find starting week day
        val cal = Calendar.getInstance().apply { time = start }
        var dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sun, 1=Mon, ..., 6=Sat
        
        val boundaryCal = Calendar.getInstance().apply { time = start }
        val limit = DateHelper.addDays(start, -7)
        while (dayOfWeek != weekStart) {
            boundaryCal.add(Calendar.DAY_OF_MONTH, -1)
            if (boundaryCal.time.before(limit)) break
            dayOfWeek = boundaryCal.get(Calendar.DAY_OF_WEEK) - 1
        }
        
        var currentWeekStart = boundaryCal.time
        while (!currentWeekStart.after(end)) {
            val weekDays = mutableListOf<Date>()
            for (s in 0 until 7) {
                val d = DateHelper.addDays(currentWeekStart, s)
                if (!d.before(start) && !d.after(end)) {
                    weekDays.add(d)
                }
            }
            if (weekDays.isNotEmpty()) {
                result.add(weekDays)
            }
            currentWeekStart = DateHelper.addDays(currentWeekStart, 7)
        }

        if (result.isEmpty()) {
            var curr = start
            while (!curr.after(end)) {
                val chunk = mutableListOf<Date>()
                for (s in 0 until 7) {
                    if (curr.after(end)) break
                    chunk.add(curr)
                    curr = DateHelper.addDays(curr, 1)
                }
                result.add(chunk)
            }
        }
        return result
    }

    /**
     * Generates a balanced schedule respecting all rules.
     */
    fun generateSchedule(
        karyakartas: List<Karyakarta>,
        shakhas: List<String>,
        rules: Rules,
        startDate: Date,
        endDate: Date
    ): List<PravasItem> {
        if (karyakartas.isEmpty() || shakhas.isEmpty() || startDate.after(endDate)) {
            return emptyList()
        }

        val weeks = splitIntoWeeks(startDate, endDate, rules.weekStart)
        val weekCount = weeks.size

        // Track per-day assignments: DateIso -> (Shakha -> Count)
        val dayShakhaCount = mutableMapOf<String, MutableMap<String, Int>>()
        // Track per-karyakarta days visited: KaryakartaId -> Set of DateIso
        val karyakartaVisitedDates = mutableMapOf<Int, MutableSet<String>>()
        // Track per-karyakarta shakhas visited: KaryakartaId -> Set of Shakha
        val karyakartaVisitedShakhas = mutableMapOf<Int, MutableSet<String>>()
        // Track per-week karyakarta assignment count: WeekIndex -> (KaryakartaId -> Count)
        val weekKaryakartaCounts = mutableMapOf<Int, MutableMap<Int, Int>>()

        karyakartas.forEach { k ->
            karyakartaVisitedDates[k.id] = mutableSetOf()
            karyakartaVisitedShakhas[k.id] = mutableSetOf()
        }
        weeks.indices.forEach { wIdx ->
            weekKaryakartaCounts[wIdx] = mutableMapOf()
        }

        // Build target shakha visit queue for each karyakarta
        val targetShakhaQueues = mutableMapOf<Int, MutableList<String>>()
        karyakartas.forEach { k ->
            val eligibleShakhas = shakhas.filter { it != k.kendriyaShakha }
            val totalVisitsNeeded = weekCount * rules.maxPerWeek
            val queue = mutableListOf<String>()

            if (rules.mustVisitAll && eligibleShakhas.isNotEmpty()) {
                queue.addAll(eligibleShakhas.shuffled())
            }
            val remaining = totalVisitsNeeded - queue.size
            if (remaining > 0 && eligibleShakhas.isNotEmpty()) {
                val extra = mutableListOf<String>()
                for (i in 0 until remaining) {
                    extra.add(eligibleShakhas.random())
                }
                queue.addAll(extra)
            }

            val finalQueue = queue.take(totalVisitsNeeded)
                .shuffled()
                .filter { it != k.kendriyaShakha }
                .toMutableList()
            targetShakhaQueues[k.id] = finalQueue
        }

        val generatedItems = mutableListOf<PravasItem>()

        // Assign week by week
        weeks.forEachIndexed { weekIdx, weekDays ->
            val shuffledKaryakartas = karyakartas.shuffled()

            shuffledKaryakartas.forEach { karyakarta ->
                val queue = targetShakhaQueues[karyakarta.id] ?: mutableListOf()
                val currentWeekCount = weekKaryakartaCounts[weekIdx]?.get(karyakarta.id) ?: 0
                val remainingVisitsInWeek = rules.maxPerWeek - currentWeekCount

                if (remainingVisitsInWeek > 0) {
                    for (v in 0 until remainingVisitsInWeek) {
                        if (queue.isEmpty()) break

                        var targetShakhaIndex = 0
                        var targetShakha = queue[targetShakhaIndex]

                        if (rules.avoidDuplicate) {
                            val visitedSet = karyakartaVisitedShakhas[karyakarta.id] ?: emptySet()
                            val unvisitedIdx = queue.indexOfFirst { !visitedSet.contains(it) }
                            if (unvisitedIdx != -1) {
                                targetShakhaIndex = unvisitedIdx
                                targetShakha = queue[targetShakhaIndex]
                            }
                        }

                        // Filter eligible days in the current week for this karyakarta & shakha
                        fun getEligibleDays(shakha: String): List<Date> {
                            return weekDays.filter { date ->
                                val cal = Calendar.getInstance().apply { time = date }
                                val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
                                val dateIso = DateHelper.formatIso(date)

                                if (!rules.globalAvailableDays.contains(dayOfWeek)) return@filter false
                                if (rules.excludeSunday && dayOfWeek == 0) return@filter false
                                if (!karyakarta.availableDays.contains(dayOfWeek)) return@filter false
                                if (karyakartaVisitedDates[karyakarta.id]?.contains(dateIso) == true) return@filter false

                                val shakhaCountOnDate = dayShakhaCount[dateIso]?.get(shakha) ?: 0
                                if (shakhaCountOnDate >= rules.maxPerShakhaPerDay) return@filter false

                                true
                            }
                        }

                        var eligibleDays = getEligibleDays(targetShakha)

                        // If no eligible days for this shakha, attempt another shakha in the queue
                        if (eligibleDays.isEmpty()) {
                            var foundAlternate = false
                            for (idx in queue.indices) {
                                val altShakha = queue[idx]
                                val altDays = getEligibleDays(altShakha)
                                if (altDays.isNotEmpty()) {
                                    targetShakha = altShakha
                                    targetShakhaIndex = idx
                                    eligibleDays = altDays
                                    foundAlternate = true
                                    break
                                }
                            }
                            if (!foundAlternate) continue
                        }

                        val chosenDay = eligibleDays.shuffled().firstOrNull() ?: continue
                        val chosenDateIso = DateHelper.formatIso(chosenDay)

                        // Record assignment
                        val shakhaMap = dayShakhaCount.getOrPut(chosenDateIso) { mutableMapOf() }
                        shakhaMap[targetShakha] = (shakhaMap[targetShakha] ?: 0) + 1

                        karyakartaVisitedDates.getOrPut(karyakarta.id) { mutableSetOf() }.add(chosenDateIso)
                        karyakartaVisitedShakhas.getOrPut(karyakarta.id) { mutableSetOf() }.add(targetShakha)

                        val weekMap = weekKaryakartaCounts.getOrPut(weekIdx) { mutableMapOf() }
                        weekMap[karyakarta.id] = (weekMap[karyakarta.id] ?: 0) + 1

                        generatedItems.add(
                            PravasItem(
                                id = "${karyakarta.id}-$chosenDateIso-$targetShakha-${UUID.randomUUID()}",
                                date = chosenDateIso,
                                karyakartaId = karyakarta.id,
                                karyakartaName = karyakarta.name,
                                shreni = karyakarta.shreni,
                                shakha = targetShakha,
                                kendriya = karyakarta.kendriyaShakha
                            )
                        )

                        queue.removeAt(targetShakhaIndex)
                    }
                }
            }
        }

        return generatedItems.sortedBy { it.date }
    }

    fun groupIntoWeeks(
        items: List<PravasItem>,
        startDate: Date,
        endDate: Date,
        weekStart: Int
    ): List<WeekGroup> {
        val weeks = splitIntoWeeks(startDate, endDate, weekStart)
        val result = mutableListOf<WeekGroup>()

        weeks.forEachIndexed { idx, weekDays ->
            val datesInWeek = weekDays.map { DateHelper.formatIso(it) }.toSet()
            val weekItems = items.filter { datesInWeek.contains(it.date) }
            if (weekDays.isNotEmpty()) {
                result.add(
                    WeekGroup(
                        index = idx + 1,
                        start = weekDays.first(),
                        end = weekDays.last(),
                        items = weekItems
                    )
                )
            }
        }
        return result
    }
}
