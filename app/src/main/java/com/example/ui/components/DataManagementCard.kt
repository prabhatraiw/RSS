package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DateHelper
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Date

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DataManagementCard(
    onLoadSaved: () -> Unit,
    onExportJson: () -> Unit,
    onImportJson: () -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "डेटा प्रबंधन",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    text = "📂 Load Saved",
                    bgColor = Color(0xFFF3F4F6),
                    textColor = TextPrimary,
                    onClick = onLoadSaved
                )
                ActionButton(
                    text = "📤 Export JSON",
                    bgColor = Color(0xFFF3F4F6),
                    textColor = TextPrimary,
                    onClick = onExportJson
                )
                ActionButton(
                    text = "📥 Import JSON",
                    bgColor = Color(0xFFF3F4F6),
                    textColor = TextPrimary,
                    onClick = onImportJson
                )
                ActionButton(
                    text = "🗑️ Clear",
                    bgColor = Color(0xFFFEF2F2),
                    textColor = Color(0xFFB91C1C),
                    borderColor = Color(0xFFFECACA),
                    onClick = onClearAll
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Local key: shakha_pravas_data • ${DateHelper.formatDisplay(Date())}",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    bgColor: Color,
    textColor: Color,
    borderColor: Color = Color(0xFFE5E7EB),
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, borderColor, CircleShape)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
