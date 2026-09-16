package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HINDI_DAY_INITIALS
import com.example.model.Rules
import com.example.ui.theme.AmberBg
import com.example.ui.theme.AmberBorder
import com.example.ui.theme.NavyDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesCard(
    rules: Rules,
    onRulesChanged: (Rules) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚙️ नियम",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFFF3F4F6))
                        .clickable { onReset() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Reset",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3 inputs: Max/Day, Max/Week, Max/Shakha/Day
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RuleNumberField(
                    label = "Max / Day",
                    value = rules.maxPerDay,
                    min = 1,
                    max = 3,
                    onValueChange = { onRulesChanged(rules.copy(maxPerDay = it)) },
                    modifier = Modifier.weight(1f)
                )
                RuleNumberField(
                    label = "Max / Week",
                    value = rules.maxPerWeek,
                    min = 1,
                    max = 7,
                    onValueChange = { onRulesChanged(rules.copy(maxPerWeek = it)) },
                    modifier = Modifier.weight(1f)
                )
                RuleNumberField(
                    label = "Max / Shakha",
                    value = rules.maxPerShakhaPerDay,
                    min = 1,
                    max = 5,
                    onValueChange = { onRulesChanged(rules.copy(maxPerShakhaPerDay = it)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Checkboxes
            RuleCheckbox(
                label = "Must visit all (except kendriya)",
                checked = rules.mustVisitAll,
                onCheckedChange = { onRulesChanged(rules.copy(mustVisitAll = it)) }
            )
            RuleCheckbox(
                label = "Avoid duplicate",
                checked = rules.avoidDuplicate,
                onCheckedChange = { onRulesChanged(rules.copy(avoidDuplicate = it)) }
            )
            RuleCheckbox(
                label = "Exclude Sunday",
                checked = rules.excludeSunday,
                onCheckedChange = { onRulesChanged(rules.copy(excludeSunday = it)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Global available days
            Text(
                text = "Global Available Days",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                HINDI_DAY_INITIALS.forEachIndexed { index, label ->
                    val isSelected = rules.globalAvailableDays.contains(index)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) SaffronPrimary else Color(0xFFF3F4F6))
                            .border(
                                width = 1.dp,
                                color = if (isSelected) SaffronPrimary else Color(0xFFE5E7EB),
                                shape = CircleShape
                            )
                            .clickable {
                                val current = rules.globalAvailableDays.toMutableSet()
                                if (current.contains(index)) current.remove(index) else current.add(index)
                                onRulesChanged(rules.copy(globalAvailableDays = current.sorted()))
                            }
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Week Start dropdown
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Week Start: ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))

                var expanded by remember { mutableStateOf(false) }
                val options = listOf("Sunday" to 0, "Monday" to 1)
                val currentText = if (rules.weekStart == 0) "Sunday" else "Monday"

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    Box(
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF9FAFB))
                            .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                            .clickable { expanded = !expanded }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = currentText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF111827)
                            )
                            Text(
                                text = if (expanded) "▲" else "▼",
                                fontSize = 9.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        options.forEach { (text, valInt) ->
                            DropdownMenuItem(
                                text = { Text(text, fontSize = 12.sp, color = Color(0xFF111827)) },
                                onClick = {
                                    onRulesChanged(rules.copy(weekStart = valInt))
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Amber summary banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AmberBg, RoundedCornerShape(8.dp))
                    .border(1.dp, AmberBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "नियम: केंद्रीय शाखा में प्रवास नहीं होगा। प्रत्येक सप्ताह अधिकतम ${rules.maxPerWeek} प्रवास। रविवार ${if (rules.excludeSunday) "बंद" else "खुला"}।",
                    fontSize = 11.sp,
                    color = Color(0xFF92400E),
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun RuleNumberField(
    label: String,
    value: Int,
    min: Int = 1,
    max: Int = 10,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(3.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF9FAFB))
                .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                    .clickable {
                        if (value > min) onValueChange(value - 1)
                    }
            ) {
                Text(
                    text = "−",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (value > min) Color(0xFF111827) else Color(0xFF9CA3AF)
                )
            }

            Text(
                text = "$value",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                    .clickable {
                        if (value < max) onValueChange(value + 1)
                    }
            ) {
                Text(
                    text = "+",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (value < max) Color(0xFF111827) else Color(0xFF9CA3AF)
                )
            }
        }
    }
}

@Composable
private fun RuleCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 1.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = SaffronPrimary,
                checkmarkColor = Color.White
            )
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextPrimary
        )
    }
}
