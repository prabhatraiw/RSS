package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.content.FileProvider
import com.example.model.DateHelper
import com.example.model.Period
import com.example.model.PravasItem
import com.example.model.WeekGroup
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min

object ImageExporter {

    private const val COLOR_BG_CREAM = 0xFFFFFBF0.toInt()
    private const val COLOR_SAFFRON = 0xFFFF6B00.toInt()
    private const val COLOR_NAVY = 0xFF1A1A2E.toInt()
    private const val COLOR_TEXT_DARK = 0xFF222222.toInt()
    private const val COLOR_LIGHT_ORANGE = 0xFFFFF7ED.toInt()
    private const val COLOR_BORDER = 0xFFE5E7EB.toInt()
    private const val COLOR_GRAY_TEXT = 0xFF4B5563.toInt()

    fun renderPosterBitmap(
        schedule: List<PravasItem>,
        period: Period,
        district: String,
        nagar: String
    ): Bitmap {
        val width = 1080
        val height = 1350
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background
        val bgPaint = Paint().apply { color = COLOR_BG_CREAM }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Header bar
        val headerPaint = Paint().apply { color = COLOR_SAFFRON }
        canvas.drawRect(0f, 0f, width.toFloat(), 130f, headerPaint)

        // Title text
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 46f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("Shakha Pravas Yojna", 36f, 68f, titlePaint)

        // Subtitle text (location and date)
        val subtitlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 22f
            isAntiAlias = true
        }
        val locStr = if (nagar.isNotBlank() || district.isNotBlank()) {
            val parts = listOf(nagar, district).filter { it.isNotBlank() }
            " (${parts.joinToString(", ")})"
        } else ""
        val dateSubtitle = "${DateHelper.formatDisplay(period.start)} से ${DateHelper.formatDisplay(period.end)} तक$locStr"
        canvas.drawText(dateSubtitle, 36f, 106f, subtitlePaint)

        // Group items by date
        val dateGroupMap = linkedMapOf<String, MutableList<PravasItem>>()
        schedule.sortedBy { it.date }.forEach { item ->
            dateGroupMap.getOrPut(item.date) { mutableListOf() }.add(item)
        }

        var currentY = 175f
        val dateTitlePaint = Paint().apply {
            color = COLOR_NAVY
            textSize = 25f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val itemPaint = Paint().apply {
            color = COLOR_TEXT_DARK
            textSize = 27f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val arrowPaint = Paint().apply {
            color = COLOR_SAFFRON
            textSize = 27f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        for ((dateIso, items) in dateGroupMap) {
            if (currentY > 1280f) break

            val displayDate = DateHelper.formatWithDay(dateIso)
            canvas.drawText(displayDate, 36f, currentY, dateTitlePaint)
            currentY += 34f

            for (it in items) {
                if (currentY > 1280f) break
                val name = it.karyakartaName
                val arrow = " → "
                val shakha = it.shakha

                canvas.drawText(name, 56f, currentY, itemPaint)
                val nameWidth = itemPaint.measureText(name)
                canvas.drawText(arrow, 56f + nameWidth, currentY, arrowPaint)
                val arrowWidth = arrowPaint.measureText(arrow)
                canvas.drawText(shakha, 56f + nameWidth + arrowWidth, currentY, arrowPaint)

                currentY += 38f
            }
            currentY += 18f
        }

        // Bottom footer
        canvas.drawRect(0f, (height - 36).toFloat(), width.toFloat(), height.toFloat(), headerPaint)
        val footerPaint = Paint().apply {
            color = Color.WHITE
            textSize = 20f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("ॐ संघ शक्ति ॐ", (width / 2).toFloat(), (height - 12).toFloat(), footerPaint)

        return bitmap
    }

    fun renderWeeklyDateBitmap(
        weekGroup: WeekGroup,
        district: String,
        nagar: String
    ): Bitmap {
        val sortedItems = weekGroup.items.sortedBy { it.date }
        val width = 1080
        val computedHeight = max(520, 280 + sortedItems.size * 64)
        val bitmap = Bitmap.createBitmap(width, computedHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgPaint = Paint().apply { color = COLOR_BG_CREAM }
        canvas.drawRect(0f, 0f, width.toFloat(), computedHeight.toFloat(), bgPaint)

        val headerPaint = Paint().apply { color = COLOR_SAFFRON }
        canvas.drawRect(0f, 0f, width.toFloat(), 195f, headerPaint)

        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 44f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("साप्ताहिक शाखा प्रवास योजना", 36f, 62f, titlePaint)

        val locPaint = Paint().apply {
            color = Color.WHITE
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val locText = "${if (nagar.isNotBlank()) nagar else "नगर/खण्ड का नाम"}, ${if (district.isNotBlank()) district else "जिला का नाम"}"
        canvas.drawText(locText, 36f, 108f, locPaint)

        val rangePaint = Paint().apply {
            color = Color.WHITE
            textSize = 26f
            isAntiAlias = true
        }
        val dateText = "Week ${weekGroup.index}: ${DateHelper.formatDisplay(weekGroup.start)} से ${DateHelper.formatDisplay(weekGroup.end)} तक"
        canvas.drawText(dateText, 36f, 154f, rangePaint)

        // Table Header
        val colHeaderPaint = Paint().apply {
            color = COLOR_NAVY
            textSize = 25f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val colShakhaPaint = Paint().apply {
            color = COLOR_SAFFRON
            textSize = 25f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("तिथि", 44f, 235f, colHeaderPaint)
        canvas.drawText("कार्यकर्ता", 320f, 235f, colHeaderPaint)
        canvas.drawText("शाखा", 760f, 235f, colShakhaPaint)

        var y = 270f
        val rowBgEven = Paint().apply { color = Color.WHITE }
        val rowBgOdd = Paint().apply { color = COLOR_LIGHT_ORANGE }

        val rowDatePaint = Paint().apply {
            color = COLOR_NAVY
            textSize = 22f
            isAntiAlias = true
        }
        val rowNamePaint = Paint().apply {
            color = COLOR_NAVY
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val rowShakhaPaint = Paint().apply {
            color = COLOR_SAFFRON
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        if (sortedItems.isEmpty()) {
            val emptyPaint = Paint().apply {
                color = COLOR_GRAY_TEXT
                textSize = 26f
                isAntiAlias = true
            }
            canvas.drawText("इस सप्ताह कोई प्रवास नहीं", 44f, y + 40f, emptyPaint)
        } else {
            sortedItems.forEachIndexed { index, item ->
                val rowRect = RectF(26f, y - 32f, (width - 26).toFloat(), y + 24f)
                canvas.drawRoundRect(rowRect, 8f, 8f, if (index % 2 == 0) rowBgEven else rowBgOdd)

                canvas.drawText(DateHelper.formatDisplay(item.date), 44f, y, rowDatePaint)
                canvas.drawText(item.karyakartaName, 320f, y, rowNamePaint)
                canvas.drawText(item.shakha, 760f, y, rowShakhaPaint)

                y += 64f
            }
        }

        // Bottom bar
        canvas.drawRect(0f, (computedHeight - 38).toFloat(), width.toFloat(), computedHeight.toFloat(), headerPaint)
        val footerPaint = Paint().apply {
            color = Color.WHITE
            textSize = 20f
            isAntiAlias = true
        }
        canvas.drawText("कुल ${sortedItems.size} प्रवास", 36f, (computedHeight - 13).toFloat(), footerPaint)

        return bitmap
    }

    fun renderWeeklyNameBitmap(
        weekGroup: WeekGroup,
        district: String,
        nagar: String
    ): Bitmap {
        val width = 1080
        val height = 1350
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgPaint = Paint().apply { color = COLOR_BG_CREAM }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        val headerPaint = Paint().apply { color = COLOR_SAFFRON }
        canvas.drawRect(0f, 0f, width.toFloat(), 195f, headerPaint)

        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 44f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("साप्ताहिक शाखा प्रवास योजना", 36f, 62f, titlePaint)

        val locPaint = Paint().apply {
            color = Color.WHITE
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val locText = "${if (nagar.isNotBlank()) nagar else "नगर/खण्ड का नाम"}, ${if (district.isNotBlank()) district else "जिला का नाम"}"
        canvas.drawText(locText, 36f, 108f, locPaint)

        val rangePaint = Paint().apply {
            color = Color.WHITE
            textSize = 26f
            isAntiAlias = true
        }
        val dateText = "Week ${weekGroup.index}: ${DateHelper.formatDisplay(weekGroup.start)} से ${DateHelper.formatDisplay(weekGroup.end)} तक"
        canvas.drawText(dateText, 36f, 154f, rangePaint)

        // Group by Karyakarta
        val personMap = linkedMapOf<Int, MutableList<PravasItem>>()
        val nameMap = mutableMapOf<Int, String>()
        weekGroup.items.sortedBy { it.date }.forEach { item ->
            personMap.getOrPut(item.karyakartaId) { mutableListOf() }.add(item)
            nameMap[item.karyakartaId] = item.karyakartaName
        }

        data class PersonCard(val id: Int, val name: String, val items: List<PravasItem>)
        val personCards = personMap.map { (id, items) ->
            PersonCard(id, nameMap[id] ?: "", items)
        }.sortedBy { it.name }

        // Grid of 3 columns
        val rows = mutableListOf<List<PersonCard>>()
        for (i in personCards.indices step 3) {
            rows.add(personCards.subList(i, min(i + 3, personCards.size)))
        }

        var startY = 215f
        val targetHeight = 1010f
        val gapY = 14f
        val rowHeight = if (rows.isNotEmpty()) max(88f, (targetHeight - (rows.size - 1) * gapY) / rows.size) else 0f

        val cardBgPaint = Paint().apply { color = Color.WHITE }
        val cardStrokePaint = Paint().apply {
            color = COLOR_BORDER
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }

        if (personCards.isEmpty()) {
            val emptyPaint = Paint().apply {
                color = COLOR_GRAY_TEXT
                textSize = 26f
                isAntiAlias = true
            }
            canvas.drawText("इस सप्ताह कोई प्रवास नहीं", 44f, 260f, emptyPaint)
        } else {
            rows.forEach { row ->
                row.forEachIndexed { colIdx, person ->
                    val cardX = 20f + colIdx * 353f
                    val cardW = 338f
                    val cardRect = RectF(cardX, startY, cardX + cardW, startY + rowHeight)
                    canvas.drawRoundRect(cardRect, 10f, 10f, cardBgPaint)
                    canvas.drawRoundRect(cardRect, 10f, 10f, cardStrokePaint)

                    val scale = min(2.0f, max(0.55f, (rowHeight - 12f) / (98f + max(0, person.items.size - 1) * 56f)))
                    val nameSize = 25f * scale
                    val dateSize = 17f * scale
                    val branchSize = 21f * scale
                    val itemStep = 54f * scale

                    val namePaint = Paint().apply {
                        color = COLOR_NAVY
                        textSize = nameSize
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        isAntiAlias = true
                    }
                    canvas.drawText(person.name, cardX + 14f, startY + 34f * scale, namePaint)

                    var itemY = startY + 44f * scale
                    val datePaint = Paint().apply {
                        color = COLOR_GRAY_TEXT
                        textSize = dateSize
                        isAntiAlias = true
                    }
                    val branchPaint = Paint().apply {
                        color = COLOR_SAFFRON
                        textSize = branchSize
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        isAntiAlias = true
                    }

                    person.items.forEach { itm ->
                        val dateObj = itm.dateObj
                        val dayName = DateHelper.getHindiDayName(dateObj)
                        val line1 = "${DateHelper.formatDisplay(dateObj)} • $dayName"
                        canvas.drawText(line1, cardX + 14f, itemY + dateSize, datePaint)
                        canvas.drawText(itm.shakha, cardX + 14f, itemY + dateSize + 28f * scale, branchPaint)
                        itemY += itemStep
                    }
                }
                startY += rowHeight + gapY
            }
        }

        // Bottom footer
        canvas.drawRect(0f, (height - 38).toFloat(), width.toFloat(), height.toFloat(), headerPaint)
        val footerPaint = Paint().apply {
            color = Color.WHITE
            textSize = 20f
            isAntiAlias = true
        }
        canvas.drawText("कुल ${weekGroup.items.size} प्रवास", 36f, (height - 13).toFloat(), footerPaint)

        return bitmap
    }

    fun shareBitmap(context: Context, bitmap: Bitmap, fileName: String, title: String) {
        try {
            val cacheDir = File(context.cacheDir, "shared").apply { mkdirs() }
            val file = File(cacheDir, "$fileName.png")
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun generateCsv(items: List<PravasItem>): String {
        val sb = StringBuilder()
        sb.append("Date,Karyakarta,Shreni,Shakha,Kendriya\n")
        items.sortedBy { it.date }.forEach { item ->
            val dateStr = DateHelper.formatDisplay(item.date)
            val nameStr = item.karyakartaName.replace("\"", "\"\"")
            val shreniStr = item.shreni.replace("\"", "\"\"")
            val shakhaStr = item.shakha.replace("\"", "\"\"")
            val kendriyaStr = item.kendriya.replace("\"", "\"\"")
            sb.append("\"$dateStr\",\"$nameStr\",\"$shreniStr\",\"$shakhaStr\",\"$kendriyaStr\"\n")
        }
        return sb.toString()
    }

    fun shareCsv(context: Context, csvData: String, fileName: String, title: String) {
        try {
            val cacheDir = File(context.cacheDir, "csv").apply { mkdirs() }
            val file = File(cacheDir, "$fileName.csv")
            file.writeText(csvData)

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
