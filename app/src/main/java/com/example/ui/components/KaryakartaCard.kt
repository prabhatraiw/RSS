package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DEFAULT_SHRENIS
import com.example.model.HINDI_DAY_INITIALS
import com.example.model.Karyakarta
import com.example.ui.theme.BorderOrange
import com.example.ui.theme.NavyDark
import com.example.ui.theme.SaffronContainer
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun KaryakartaCard(
    karyakartas: List<Karyakarta>,
    shakhas: List<String>,
    expandedDaysIds: Set<Int>,
    onUpdateKaryakarta: (Karyakarta) -> Unit,
    onRemoveKaryakarta: (Int) -> Unit,
    onToggleDay: (Int, Int) -> Unit,
    onToggleDaysExpanded: (Int) -> Unit,
    onAddKaryakarta: (name: String, shreni: String, kendriya: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👥 कार्यकर्ता (${karyakartas.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "नाम पर टैप कर संपादित करें",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // List of Karyakartas (clean, unclipped, flows with the parent screen scroll)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                karyakartas.forEachIndexed { index, k ->
                    KaryakartaItemRow(
                        index = index + 1,
                        karyakarta = k,
                        shakhas = shakhas,
                        isExpanded = expandedDaysIds.contains(k.id),
                        onUpdate = onUpdateKaryakarta,
                        onRemove = { onRemoveKaryakarta(k.id) },
                        onToggleDay = { day -> onToggleDay(k.id, day) },
                        onToggleExpanded = { onToggleDaysExpanded(k.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Add button
            Button(
                onClick = {
                    onAddKaryakarta("नया कार्यकर्ता", "Sangathan Shreni", shakhas.firstOrNull() ?: "")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
            ) {
                Text(
                    text = "+ नया कार्यकर्ता जोड़ें",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun KaryakartaItemRow(
    index: Int,
    karyakarta: Karyakarta,
    shakhas: List<String>,
    isExpanded: Boolean,
    onUpdate: (Karyakarta) -> Unit,
    onRemove: () -> Unit,
    onToggleDay: (Int) -> Unit,
    onToggleExpanded: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SaffronContainer)
            .border(1.dp, BorderOrange, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Row 1: Index Avatar + Editable Name + Remove button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Index Avatar Circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(SaffronPrimary)
                ) {
                    Text(
                        text = "$index",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Name TextField with explicit background, border and dark text color
                BasicTextField(
                    value = karyakarta.name,
                    onValueChange = { onUpdate(karyakarta.copy(name = it)) },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    ),
                    cursorBrush = SolidColor(SaffronPrimary),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 9.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (karyakarta.name.isEmpty()) {
                                Text(
                                    text = "कार्यकर्ता का नाम",
                                    fontSize = 14.sp,
                                    color = Color(0xFF9CA3AF)
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Delete button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                        .clickable { onRemove() }
                ) {
                    Text(
                        text = "✕",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2626)
                    )
                }
            }

            // Row 2: Shreni and Kendriya Shakha dropdown selectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Shreni Dropdown
                var shreniExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = shreniExpanded,
                    onExpandedChange = { shreniExpanded = !shreniExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                            .clickable { shreniExpanded = !shreniExpanded }
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = karyakarta.shreni,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF111827),
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = if (shreniExpanded) "▲" else "▼",
                                fontSize = 9.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                    ExposedDropdownMenu(
                        expanded = shreniExpanded,
                        onDismissRequest = { shreniExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DEFAULT_SHRENIS.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s, fontSize = 12.sp, color = Color(0xFF111827)) },
                                onClick = {
                                    onUpdate(karyakarta.copy(shreni = s))
                                    shreniExpanded = false
                                }
                            )
                        }
                    }
                }

                // Kendriya Shakha Dropdown
                var shakhaExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = shakhaExpanded,
                    onExpandedChange = { shakhaExpanded = !shakhaExpanded },
                    modifier = Modifier.weight(1.2f)
                ) {
                    Box(
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                            .clickable { shakhaExpanded = !shakhaExpanded }
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "केंद्रीय: ${karyakarta.kendriyaShakha.ifBlank { "कोई नहीं" }}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = SaffronDark,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = if (shakhaExpanded) "▲" else "▼",
                                fontSize = 9.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                    ExposedDropdownMenu(
                        expanded = shakhaExpanded,
                        onDismissRequest = { shakhaExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        shakhas.forEach { sh ->
                            DropdownMenuItem(
                                text = { Text(sh, fontSize = 12.sp, color = Color(0xFF111827)) },
                                onClick = {
                                    onUpdate(karyakarta.copy(kendriyaShakha = sh))
                                    shakhaExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Row 3: Available days toggle bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFD1D5DB), CircleShape)
                        .clickable { onToggleExpanded() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "उपलब्ध दिन (${karyakarta.availableDays.size}/7) ${if (isExpanded) "▲" else "▼"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }

                // Preview dots
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    karyakarta.availableDays.forEach { _ ->
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(SaffronPrimary)
                        )
                    }
                }
            }

            // Expanded day buttons
            AnimatedVisibility(visible = isExpanded) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    (0..6).forEach { dayIndex ->
                        val isDaySelected = karyakarta.availableDays.contains(dayIndex)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isDaySelected) SaffronPrimary else Color.White)
                                .border(
                                    width = 1.dp,
                                    color = if (isDaySelected) SaffronPrimary else Color(0xFFD1D5DB),
                                    shape = CircleShape
                                )
                                .clickable { onToggleDay(dayIndex) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = HINDI_DAY_INITIALS[dayIndex],
                                color = if (isDaySelected) Color.White else Color(0xFF111827),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

