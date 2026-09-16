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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.db.SavedPlanEntity
import com.example.model.DateHelper
import com.example.ui.theme.BorderOrange
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.SaffronContainer
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedPlansScreen(
    savedPlans: List<SavedPlanEntity>,
    currentDistrict: String,
    currentNagar: String,
    currentStartDate: String,
    currentEndDate: String,
    currentVisitsCount: Int,
    onSaveCurrentPlan: (String) -> Unit,
    onLoadPlan: (SavedPlanEntity) -> Unit,
    onDeletePlan: (Long) -> Unit,
    onLoadSavedPreferences: () -> Unit,
    onExportJson: () -> Unit,
    onImportJson: () -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var planTitleInput by remember {
        mutableStateOf(
            if (currentNagar.isNotBlank()) "$currentNagar प्रवास योजना (${DateHelper.formatDisplay(currentStartDate)})"
            else "प्रवास योजना (${DateHelper.formatDisplay(currentStartDate)})"
        )
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Save current plan card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = SaffronPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "स्थानीय डेटाबेस में योजना सहेजें (Room)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "वर्तमान योजना ($currentVisitsCount प्रवास, ${DateHelper.formatDisplay(currentStartDate)} से ${DateHelper.formatDisplay(currentEndDate)}) को एक नाम देकर सुरक्षित रखें।",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = planTitleInput,
                        onValueChange = { planTitleInput = it },
                        placeholder = { Text("योजना का शीर्षक दर्ज करें", fontSize = 12.sp, color = Color(0xFF9CA3AF)) },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 13.sp, color = Color(0xFF111827)),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF111827),
                            unfocusedTextColor = Color(0xFF111827),
                            focusedBorderColor = SaffronPrimary,
                            unfocusedBorderColor = Color(0xFFD1D5DB)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (planTitleInput.isNotBlank()) {
                                onSaveCurrentPlan(planTitleInput.trim())
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                    ) {
                        Text("सहेजें", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Saved plans list
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "📂 सहेजी गई योजनाएँ (${savedPlans.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (savedPlans.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Text(
                            text = "अभी कोई सहेजी गई योजना नहीं है। ऊपर से योजना सहेजें!",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val timeFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

                        savedPlans.forEach { plan ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SaffronContainer)
                                    .border(1.dp, BorderOrange, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = plan.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${if (plan.nagar.isNotBlank()) "${plan.nagar} • " else ""}${DateHelper.formatDisplay(plan.startDate)} - ${DateHelper.formatDisplay(plan.endDate)} • ${plan.totalVisits} प्रवास",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = "सहेजा गया: ${timeFormat.format(Date(plan.timestamp))}",
                                            fontSize = 9.sp,
                                            color = Color(0xFF9CA3AF),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = { onLoadPlan(plan) },
                                            colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                                horizontal = 10.dp,
                                                vertical = 6.dp
                                            ),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text("लोड करें", color = Color.White, fontSize = 11.sp)
                                        }

                                        IconButton(
                                            onClick = { onDeletePlan(plan.id) },
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Color(0xFFDC2626),
                                                modifier = Modifier.size(18.dp)
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

        // Additional Data Management Card
        DataManagementCard(
            onLoadSaved = onLoadSavedPreferences,
            onExportJson = onExportJson,
            onImportJson = onImportJson,
            onClearAll = onClearAll
        )
    }
}
