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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.theme.NavyDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ShakhaCard(
    district: String,
    nagar: String,
    shakhas: List<String>,
    onDistrictChanged: (String) -> Unit,
    onNagarChanged: (String) -> Unit,
    onAddShakha: (String) -> Unit,
    onUpdateShakha: (oldName: String, newName: String) -> Unit,
    onRemoveShakha: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var newShakhaText by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // District / Nagar Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "📍 संगठन क्षेत्र",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "जिला का नाम",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        OutlinedTextField(
                            value = district,
                            onValueChange = onDistrictChanged,
                            placeholder = { Text("जिला", fontSize = 12.sp, color = Color(0xFF9CA3AF)) },
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 13.sp, color = Color(0xFF111827), fontWeight = FontWeight.Medium),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF111827),
                                unfocusedTextColor = Color(0xFF111827),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = SaffronPrimary,
                                unfocusedBorderColor = Color(0xFFD1D5DB)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "नगर/खण्ड का नाम",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        OutlinedTextField(
                            value = nagar,
                            onValueChange = onNagarChanged,
                            placeholder = { Text("नगर/खण्ड", fontSize = 12.sp, color = Color(0xFF9CA3AF)) },
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 13.sp, color = Color(0xFF111827), fontWeight = FontWeight.Medium),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF111827),
                                unfocusedTextColor = Color(0xFF111827),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = SaffronPrimary,
                                unfocusedBorderColor = Color(0xFFD1D5DB)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        }

        // Shakhas Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "🏠 शाखा (${shakhas.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "सभी शाखाएँ ${if (nagar.isNotBlank()) nagar else "चयनित नगर/खण्ड"} के अंतर्गत हैं।",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Shakha items list (flows naturally without nested scrolling conflicts)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    shakhas.forEach { shakha ->
                        ShakhaItemRow(
                            shakha = shakha,
                            onUpdate = { onUpdateShakha(shakha, it) },
                            onRemove = { onRemoveShakha(shakha) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Add new shakha row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newShakhaText,
                        onValueChange = { newShakhaText = it },
                        placeholder = { Text("नई शाखा", fontSize = 12.sp, color = Color(0xFF9CA3AF)) },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 13.sp, color = Color(0xFF111827)),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF111827),
                            unfocusedTextColor = Color(0xFF111827),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = SaffronPrimary,
                            unfocusedBorderColor = Color(0xFFD1D5DB)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Button(
                        onClick = {
                            if (newShakhaText.isNotBlank()) {
                                onAddShakha(newShakhaText)
                                newShakhaText = ""
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Text(
                            text = "जोड़ें",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShakhaItemRow(
    shakha: String,
    onUpdate: (String) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF9FAFB))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.text.BasicTextField(
            value = shakha,
            onValueChange = onUpdate,
            singleLine = true,
            textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary),
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    if (shakha.isEmpty()) {
                        Text("शाखा का नाम", fontSize = 13.sp, color = Color(0xFF9CA3AF))
                    }
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.width(4.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                .clickable { onRemove() }
        ) {
            Text("✕", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
        }
    }
}
