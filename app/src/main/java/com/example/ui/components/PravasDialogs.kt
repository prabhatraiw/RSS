package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DEFAULT_SHRENIS
import com.example.ui.theme.NavyDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddKaryakartaDialog(
    shakhas: List<String>,
    onDismiss: () -> Unit,
    onAdd: (name: String, shreni: String, kendriya: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var shreni by remember { mutableStateOf(DEFAULT_SHRENIS[0]) }
    var kendriya by remember { mutableStateOf(shakhas.firstOrNull() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "नया कार्यकर्ता जोड़ें",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = TextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("कार्यकर्ता का नाम") },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp, color = Color(0xFF111827)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF111827),
                        unfocusedTextColor = Color(0xFF111827),
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = Color(0xFFD1D5DB)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                // Shreni Dropdown
                var shreniExp by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = shreniExp,
                    onExpandedChange = { shreniExp = !shreniExp }
                ) {
                    OutlinedTextField(
                        value = shreni,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("श्रेणी") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shreniExp) },
                        textStyle = TextStyle(fontSize = 14.sp, color = Color(0xFF111827)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF111827),
                            unfocusedTextColor = Color(0xFF111827),
                            focusedBorderColor = SaffronPrimary,
                            unfocusedBorderColor = Color(0xFFD1D5DB)
                        ),
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = shreniExp,
                        onDismissRequest = { shreniExp = false }
                    ) {
                        DEFAULT_SHRENIS.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s, color = Color(0xFF111827)) },
                                onClick = {
                                    shreni = s
                                    shreniExp = false
                                }
                            )
                        }
                    }
                }

                // Kendriya Shakha Dropdown
                var shakhaExp by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = shakhaExp,
                    onExpandedChange = { shakhaExp = !shakhaExp }
                ) {
                    OutlinedTextField(
                        value = kendriya,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("केंद्रीय शाखा") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shakhaExp) },
                        textStyle = TextStyle(fontSize = 14.sp, color = Color(0xFF111827)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF111827),
                            unfocusedTextColor = Color(0xFF111827),
                            focusedBorderColor = SaffronPrimary,
                            unfocusedBorderColor = Color(0xFFD1D5DB)
                        ),
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = shakhaExp,
                        onDismissRequest = { shakhaExp = false }
                    ) {
                        shakhas.forEach { sh ->
                            DropdownMenuItem(
                                text = { Text(sh, color = Color(0xFF111827)) },
                                onClick = {
                                    kendriya = sh
                                    shakhaExp = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(name.trim(), shreni, kendriya)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
            ) {
                Text("जोड़ें", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करें", color = TextPrimary)
            }
        }
    )
}

@Composable
fun ImportJsonDialog(
    onDismiss: () -> Unit,
    onImport: (String) -> Unit
) {
    var jsonText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "📥 JSON डेटा आयात करें",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = TextPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "कृपया सहेजा गया JSON यहाँ चिपकाएँ:",
                    fontSize = 12.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = jsonText,
                    onValueChange = { jsonText = it },
                    placeholder = { Text("{\"karyakartas\": [...]}", fontSize = 11.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    textStyle = TextStyle(fontSize = 11.sp),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (jsonText.isNotBlank()) {
                        onImport(jsonText.trim())
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
            ) {
                Text("आयात करें", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करें", color = TextPrimary)
            }
        }
    )
}

@Composable
fun ExportJsonDialog(
    jsonString: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "📤 JSON डेटा निर्यात",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = TextPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "आप इस डेटा को कॉपी या शेयर कर सकते हैं:",
                    fontSize = 12.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = jsonString,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .verticalScroll(rememberScrollState()),
                    textStyle = TextStyle(fontSize = 10.sp),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Pravas JSON", jsonString)
                        clipboard.setPrimaryClip(clip)
                    }
                ) {
                    Text("कॉपी करें")
                }

                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, jsonString)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share JSON"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("शेयर करें", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("बंद करें")
            }
        }
    )
}

@Composable
fun ClearConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🗑️ डेटा रीसेट की पुष्टि",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color(0xFFB91C1C)
            )
        },
        text = {
            Text(
                text = "क्या आप निश्चित रूप से सभी सहेजा गया डेटा हटाकर डिफ़ॉल्ट पर रीसेट करना चाहते हैं?",
                fontSize = 13.sp,
                color = TextPrimary
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C))
            ) {
                Text("हाँ, रीसेट करें", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करें")
            }
        }
    )
}
