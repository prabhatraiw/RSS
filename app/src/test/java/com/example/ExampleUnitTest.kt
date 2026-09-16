package com.example

import com.example.logic.PravasGenerator
import com.example.model.Karyakarta
import com.example.model.Period
import com.example.model.Rules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testPravasGenerator_basicRules() {
    val karyakartas = listOf(
      Karyakarta(id = 1, name = "K1", shreni = "Sangathan Shreni", kendriyaShakha = "Shakha A", availableDays = listOf(0, 1, 2, 3, 4, 5, 6)),
      Karyakarta(id = 2, name = "K2", shreni = "Sangathan Shreni", kendriyaShakha = "Shakha B", availableDays = listOf(0, 1, 2, 3, 4, 5, 6))
    )
    val shakhas = listOf("Shakha A", "Shakha B", "Shakha C")
    val rules = Rules(
      maxPerDay = 1,
      maxPerWeek = 3,
      maxPerShakhaPerDay = 1,
      mustVisitAll = true,
      avoidDuplicate = true,
      excludeSunday = true,
      globalAvailableDays = listOf(0, 1, 2, 3, 4, 5, 6),
      weekStart = 0
    )
    val period = Period(start = "2026-09-01", end = "2026-09-07")

    val startDate = com.example.model.DateHelper.parseIso(period.start)!!
    val endDate = com.example.model.DateHelper.parseIso(period.end)!!
    val schedule = PravasGenerator.generateSchedule(karyakartas, shakhas, rules, startDate, endDate)

    assertTrue("Schedule should not be empty", schedule.isNotEmpty())

    // Kendriya shakha rule: no one should visit their own kendriya shakha
    schedule.forEach { item ->
      val k = karyakartas.find { it.id == item.karyakartaId }!!
      assertFalse("Karyakarta should not visit kendriya shakha", item.shakha == k.kendriyaShakha)
    }
  }

  @Test
  fun testSavedPlanEntityCreation() {
    val entity = com.example.data.db.SavedPlanEntity(
      id = 1L,
      title = "Test Plan",
      district = "Varanasi",
      nagar = "Kashi",
      startDate = "2026-09-01",
      endDate = "2026-09-14",
      totalVisits = 10,
      scheduleJson = "[]",
      karyakartasJson = "[]",
      shakhasJson = "[]",
      rulesJson = "{}"
    )
    assertEquals("Test Plan", entity.title)
    assertEquals(10, entity.totalVisits)
  }
}

