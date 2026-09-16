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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DEFAULT_SHRENIS
import com.example.model.Karyakarta
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FilterTabBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    filterShreni: String,
    onFilterShreniChanged: (String) -> Unit,
    filterShakha: String,
    onFilterShakhaChanged: (String) -> Unit,
    filterKaryakarta: String,
    onFilterKaryakartaChanged: (String) -> Unit,
    shakhas: List<String>,
    karyakartas: List<Karyakarta>,
    onExportCsv: () -> Unit,
    onExportPosterPng: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // View Mode Tab Buttons (Horizontal scrollable)
            Text(
                text = "दृश्य (View):",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "calendar" to "Calendar",
                    "table" to "Table",
                    "matrix" to "Matrix",
                    "weekly" to "Weekly Date wise",
                    "weeklyName" to "Weekly Name wise"
                ).forEach { (key, label) ->
                    val isSelected = selectedTab == key
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) NavyDark else Color(0xFFF3F4F6))
                            .clickable { onTabSelected(key) }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filters Section Header
            Text(
                text = "फ़िल्टर (Filters):",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Filters & Action Buttons (FlowRow or Wrapped)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Shreni Dropdown with visible header
                Column {
                    Text(
                        text = "श्रेणी",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                    )
                    var shreniExp by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = shreniExp,
                        onExpandedChange = { shreniExp = !shreniExp }
                    ) {
                        val label = if (filterShreni == "all") "सभी श्रेणी" else filterShreni
                        Box(
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF9FAFB))
                                .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                                .clickable { shreniExp = !shreniExp }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF111827)
                                )
                                Text(
                                    text = if (shreniExp) "▲" else "▼",
                                    fontSize = 9.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }
                        ExposedDropdownMenu(
                            expanded = shreniExp,
                            onDismissRequest = { shreniExp = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("सभी श्रेणी", fontSize = 12.sp, color = Color(0xFF111827)) },
                                onClick = {
                                    onFilterShreniChanged("all")
                                    shreniExp = false
                                }
                            )
                            DEFAULT_SHRENIS.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s, fontSize = 12.sp, color = Color(0xFF111827)) },
                                    onClick = {
                                        onFilterShreniChanged(s)
                                        shreniExp = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 2. Shakha Dropdown with visible header
                Column {
                    Text(
                        text = "शाखा",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                    )
                    var shakhaExp by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = shakhaExp,
                        onExpandedChange = { shakhaExp = !shakhaExp }
                    ) {
                        val label = if (filterShakha == "all") "सभी शाखा" else filterShakha
                        Box(
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF9FAFB))
                                .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                                .clickable { shakhaExp = !shakhaExp }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF111827)
                                )
                                Text(
                                    text = if (shakhaExp) "▲" else "▼",
                                    fontSize = 9.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }
                        ExposedDropdownMenu(
                            expanded = shakhaExp,
                            onDismissRequest = { shakhaExp = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("सभी शाखा", fontSize = 12.sp, color = Color(0xFF111827)) },
                                onClick = {
                                    onFilterShakhaChanged("all")
                                    shakhaExp = false
                                }
                            )
                            shakhas.forEach { sh ->
                                DropdownMenuItem(
                                    text = { Text(sh, fontSize = 12.sp, color = Color(0xFF111827)) },
                                    onClick = {
                                        onFilterShakhaChanged(sh)
                                        shakhaExp = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 3. Karyakarta Dropdown with visible header
                Column {
                    Text(
                        text = "कार्यकर्ता",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                    )
                    var karyakartaExp by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = karyakartaExp,
                        onExpandedChange = { karyakartaExp = !karyakartaExp }
                    ) {
                        val currentName = karyakartas.find { it.id.toString() == filterKaryakarta }?.name
                        val label = if (filterKaryakarta == "all" || currentName == null) "सभी कार्यकर्ता" else currentName
                        Box(
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF9FAFB))
                                .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                                .clickable { karyakartaExp = !karyakartaExp }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF111827)
                                )
                                Text(
                                    text = if (karyakartaExp) "▲" else "▼",
                                    fontSize = 9.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }
                        ExposedDropdownMenu(
                            expanded = karyakartaExp,
                            onDismissRequest = { karyakartaExp = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("सभी कार्यकर्ता", fontSize = 12.sp, color = Color(0xFF111827)) },
                                onClick = {
                                    onFilterKaryakartaChanged("all")
                                    karyakartaExp = false
                                }
                            )
                            karyakartas.forEach { k ->
                                DropdownMenuItem(
                                    text = { Text(k.name, fontSize = 12.sp, color = Color(0xFF111827)) },
                                    onClick = {
                                        onFilterKaryakartaChanged(k.id.toString())
                                        karyakartaExp = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Action buttons (CSV, PNG 4:5, Save)
                Column {
                    Text(
                        text = "एक्सपोर्ट व सेव",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GreenSuccess)
                                .clickable { onExportCsv() }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "CSV",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SaffronPrimary)
                                .clickable { onExportPosterPng() }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "PNG 4:5",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NavyDark)
                                .clickable { onSave() }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "💾 Save",
                                color = Color.White,
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
