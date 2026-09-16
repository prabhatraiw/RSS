package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DateHelper
import com.example.model.Karyakarta
import com.example.model.Period
import com.example.model.PravasItem
import com.example.model.WeekGroup
import com.example.ui.theme.BorderOrange
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.SaffronContainer
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EmptyScheduleView(
    period: Period,
    totalDays: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🗓️",
            fontSize = 44.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "कोई योजना नहीं",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "नियम सेट करें और \"योजना बनाएँ\" दबाएँ",
            fontSize = 12.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${DateHelper.formatDisplay(period.start)} → ${DateHelper.formatDisplay(period.end)} • $totalDays दिन",
            fontSize = 11.sp,
            color = Color(0xFF9CA3AF)
        )
    }
}

@Composable
fun CalendarScheduleView(
    items: List<PravasItem>,
    modifier: Modifier = Modifier
) {
    val groupedByDate = items.groupBy { it.date }.toSortedMap()

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        groupedByDate.forEach { (dateIso, dayItems) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color.White, Color(0xFFFFF7ED).copy(alpha = 0.4f))
                            )
                        )
                        .border(1.dp, BorderOrange, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    // Date header row with badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = DateHelper.formatWithDay(dateIso),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(NavyDark)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${dayItems.size}",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Karyakarta rows
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        dayItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.karyakartaName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = item.shakha,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableScheduleView(
    items: List<PravasItem>,
    modifier: Modifier = Modifier
) {
    val sortedItems = items.sortedBy { it.date }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .background(NavyDark)
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("तिथि", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))
                Text("कार्यकर्ता", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(140.dp))
                Text("श्रेणी", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
                Text("शाखा", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(130.dp))
            }

            // Data Rows
            sortedItems.forEachIndexed { index, item ->
                val bg = if (index % 2 == 0) Color.White else Color(0xFFF9FAFB)
                Row(
                    modifier = Modifier
                        .background(bg)
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .border(width = 0.5.dp, color = Color(0xFFF3F4F6)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = DateHelper.formatDisplay(item.date),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.width(90.dp)
                    )
                    Text(
                        text = item.karyakartaName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.width(140.dp)
                    )
                    Text(
                        text = item.shreni,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.width(120.dp)
                    )
                    Text(
                        text = item.shakha,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary,
                        modifier = Modifier.width(130.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MatrixScheduleView(
    items: List<PravasItem>,
    karyakartas: List<Karyakarta>,
    shakhas: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .background(Color(0xFFF9FAFB))
                    .padding(vertical = 8.dp, horizontal = 6.dp)
                    .border(width = 1.dp, color = Color(0xFFE5E7EB)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "कार्यकर्ता",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.width(140.dp)
                )
                shakhas.forEach { shakha ->
                    Text(
                        text = shakha,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(85.dp)
                    )
                }
            }

            // Karyakarta Rows
            karyakartas.forEach { k ->
                val visits = items.filter { it.karyakartaId == k.id }
                val visitCounts = visits.groupingBy { it.shakha }.eachCount()

                Row(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(vertical = 6.dp, horizontal = 6.dp)
                        .border(width = 0.5.dp, color = Color(0xFFE5E7EB)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.width(140.dp)) {
                        Text(
                            text = k.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${k.kendriyaShakha} • ${k.shreni}",
                            fontSize = 9.sp,
                            color = TextSecondary
                        )
                    }

                    shakhas.forEach { shakha ->
                        val isKendriya = shakha == k.kendriyaShakha
                        val count = visitCounts[shakha] ?: 0

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .width(85.dp)
                                .height(38.dp)
                                .background(if (isKendriya) Color(0xFFE5E7EB) else Color.White)
                                .border(0.5.dp, Color(0xFFF3F4F6))
                        ) {
                            when {
                                isKendriya -> Text("🏠", fontSize = 14.sp)
                                count > 0 -> Text("✓ $count", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenSuccess)
                                else -> Text("✗", fontSize = 11.sp, color = Color(0xFF9CA3AF))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeeklyDateScheduleView(
    weekGroups: List<WeekGroup>,
    karyakartas: List<Karyakarta>,
    onDownloadWeekPng: (WeekGroup) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        weekGroups.forEach { week ->
            val sortedItems = week.items.sortedBy { it.date }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    // Week Header Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF3F4F6))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Week ${week.index}: ${DateHelper.formatDisplay(week.start)} to ${DateHelper.formatDisplay(week.end)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(NavyDark)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${week.items.size} प्रवास",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(SaffronPrimary)
                                    .clickable { onDownloadWeekPng(week) }
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "⬇ PNG",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Date-wise items in week
                    if (sortedItems.isEmpty()) {
                        Text(
                            text = "इस सप्ताह कोई प्रवास नहीं",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            sortedItems.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White)
                                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = DateHelper.formatDisplay(item.date),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.width(88.dp)
                                    )
                                    Text(
                                        text = item.karyakartaName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = item.shakha,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SaffronPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Per-karyakarta summary pills
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            karyakartas.forEach { k ->
                                val count = week.items.count { it.karyakartaId == k.id }
                                if (count > 0) {
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(SaffronContainer)
                                            .border(1.dp, BorderOrange, CircleShape)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "${k.name}: $count",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyNameScheduleView(
    weekGroups: List<WeekGroup>,
    onDownloadWeekNamePng: (WeekGroup) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        weekGroups.forEach { week ->
            val personGroups = week.items
                .groupBy { it.karyakartaId }
                .map { (_, items) -> items.first().karyakartaName to items.sortedBy { it.date } }
                .sortedBy { it.first }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    // Header Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF3F4F6))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Week ${week.index}: ${DateHelper.formatDisplay(week.start)} to ${DateHelper.formatDisplay(week.end)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(NavyDark)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${week.items.size} प्रवास",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(SaffronPrimary)
                                    .clickable { onDownloadWeekNamePng(week) }
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "⬇ PNG",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (personGroups.isEmpty()) {
                        Text(
                            text = "इस सप्ताह कोई प्रवास नहीं",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            personGroups.forEach { (name, personItems) ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF9FAFB))
                                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        personItems.forEach { item ->
                                            val dayName = DateHelper.getHindiDayName(item.dateObj)
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 2.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${DateHelper.formatDisplay(item.date)} • $dayName",
                                                    fontSize = 12.sp,
                                                    color = TextSecondary
                                                )
                                                Text(
                                                    text = item.shakha,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SaffronPrimary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
