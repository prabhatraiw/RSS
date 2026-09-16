package com.example.data

import android.content.Context
import com.example.model.AppData
import com.example.model.DateHelper
import com.example.model.Karyakarta
import com.example.model.Period
import com.example.model.PravasItem
import com.example.model.Rules
import org.json.JSONArray
import org.json.JSONObject

class PravasRepository(context: Context) {

    private val prefs = context.getSharedPreferences("shakha_pravas_prefs", Context.MODE_PRIVATE)

    companion object {
        val DEFAULT_KARYAKARTAS = listOf(
            Karyakarta(1, "Jaspal Singh", "Sangathan Shreni", "Banda Bahadur", listOf(1, 2, 3, 4, 5, 6)),
            Karyakarta(2, "Prabhat Rai", "Sangathan Shreni", "Swami Vivekanand", listOf(1, 2, 3, 4, 5, 6)),
            Karyakarta(3, "Vijay Verma", "Jagran Shreni", "Chhatrapati Shivaji", listOf(1, 2, 3, 4, 5, 6)),
            Karyakarta(4, "Pradeep Mishra", "Gatividhi", "Nilkanth Mahadev", listOf(1, 2, 3, 4, 5, 6)),
            Karyakarta(5, "Umesh Garg", "Sangathan Shreni", "Maharshi Dayanand", listOf(1, 2, 3, 4, 5, 6)),
            Karyakarta(6, "अमित कालिया जी", "Jagran Shreni", "Shaheed Bhagat Singh", listOf(1, 2, 3, 4, 5, 6)),
            Karyakarta(7, "मोहनलाल जी", "Gatividhi", "Guru Golwalkar", listOf(1, 2, 3, 4, 5, 6)),
            Karyakarta(8, "सोनू शर्मा जी", "Sangathan Shreni", "Raja Ramchandra", listOf(1, 2, 3, 4, 5, 6)),
            Karyakarta(9, "सतीश जी", "Jagran Shreni", "Banda Bahadur", listOf(1, 2, 3, 4, 5, 6)),
            Karyakarta(10, "उमेश डाडा जी", "Gatividhi", "Swami Vivekanand", listOf(1, 2, 3, 4, 5, 6)),
            Karyakarta(11, "रत्नेश गोस्वामी जी", "Sangathan Shreni", "Chhatrapati Shivaji", listOf(1, 2, 3, 4, 5, 6))
        )

        val DEFAULT_SHAKHAS = listOf(
            "Banda Bahadur",
            "Swami Vivekanand",
            "Chhatrapati Shivaji",
            "Nilkanth Mahadev",
            "Maharshi Dayanand",
            "Shaheed Bhagat Singh",
            "Guru Golwalkar",
            "Raja Ramchandra"
        )
    }

    fun loadData(): AppData {
        val jsonStr = prefs.getString("shakha_pravas_data_v1", null) ?: return defaultData()
        return try {
            parseJson(jsonStr)
        } catch (_: Exception) {
            defaultData()
        }
    }

    fun saveData(data: AppData) {
        val jsonStr = toJson(data)
        prefs.edit().putString("shakha_pravas_data_v1", jsonStr).apply()
    }

    fun clearData() {
        prefs.edit().remove("shakha_pravas_data_v1").apply()
    }

    fun defaultData(): AppData {
        return AppData(
            karyakartas = DEFAULT_KARYAKARTAS,
            shakhas = DEFAULT_SHAKHAS,
            district = "",
            nagar = "",
            rules = Rules(),
            period = DateHelper.defaultPeriod(),
            schedule = emptyList()
        )
    }

    fun toJson(data: AppData): String {
        val root = JSONObject()

        val kArray = JSONArray()
        data.karyakartas.forEach { k ->
            val obj = JSONObject()
            obj.put("id", k.id)
            obj.put("name", k.name)
            obj.put("shreni", k.shreni)
            obj.put("kendriyaShakha", k.kendriyaShakha)
            val dArr = JSONArray()
            k.availableDays.forEach { dArr.put(it) }
            obj.put("availableDays", dArr)
            kArray.put(obj)
        }
        root.put("karyakartas", kArray)

        val sArray = JSONArray()
        data.shakhas.forEach { sArray.put(it) }
        root.put("shakhas", sArray)

        root.put("district", data.district)
        root.put("nagar", data.nagar)

        val rObj = JSONObject().apply {
            put("maxPerDay", data.rules.maxPerDay)
            put("maxPerWeek", data.rules.maxPerWeek)
            put("maxPerShakhaPerDay", data.rules.maxPerShakhaPerDay)
            put("mustVisitAll", data.rules.mustVisitAll)
            put("avoidDuplicate", data.rules.avoidDuplicate)
            put("excludeSunday", data.rules.excludeSunday)
            put("weekStart", data.rules.weekStart)
            val gArr = JSONArray()
            data.rules.globalAvailableDays.forEach { gArr.put(it) }
            put("globalAvailableDays", gArr)
        }
        root.put("rules", rObj)

        val pObj = JSONObject().apply {
            put("start", data.period.start)
            put("end", data.period.end)
        }
        root.put("period", pObj)

        val schedArray = JSONArray()
        data.schedule.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("date", item.date)
                put("karyakartaId", item.karyakartaId)
                put("karyakartaName", item.karyakartaName)
                put("shreni", item.shreni)
                put("shakha", item.shakha)
                put("kendriya", item.kendriya)
            }
            schedArray.put(obj)
        }
        root.put("schedule", schedArray)

        return root.toString(2)
    }

    fun parseJson(jsonStr: String): AppData {
        val root = JSONObject(jsonStr)

        val karyakartas = mutableListOf<Karyakarta>()
        val kArray = root.optJSONArray("karyakartas")
        if (kArray != null) {
            for (i in 0 until kArray.length()) {
                val obj = kArray.getJSONObject(i)
                val days = mutableListOf<Int>()
                val dArr = obj.optJSONArray("availableDays")
                if (dArr != null) {
                    for (j in 0 until dArr.length()) {
                        days.add(dArr.getInt(j))
                    }
                } else {
                    days.addAll(listOf(1, 2, 3, 4, 5, 6))
                }
                karyakartas.add(
                    Karyakarta(
                        id = obj.optInt("id", i + 1),
                        name = obj.optString("name", "कार्यकर्ता"),
                        shreni = obj.optString("shreni", "Sangathan Shreni"),
                        kendriyaShakha = obj.optString("kendriyaShakha", ""),
                        availableDays = days
                    )
                )
            }
        }

        val shakhas = mutableListOf<String>()
        val sArray = root.optJSONArray("shakhas")
        if (sArray != null) {
            for (i in 0 until sArray.length()) {
                shakhas.add(sArray.getString(i))
            }
        }

        val district = root.optString("district", "")
        val nagar = root.optString("nagar", "")

        val rObj = root.optJSONObject("rules")
        val rules = if (rObj != null) {
            val gDays = mutableListOf<Int>()
            val gArr = rObj.optJSONArray("globalAvailableDays")
            if (gArr != null) {
                for (j in 0 until gArr.length()) {
                    gDays.add(gArr.getInt(j))
                }
            } else {
                gDays.addAll(listOf(1, 2, 3, 4, 5, 6))
            }
            Rules(
                maxPerDay = rObj.optInt("maxPerDay", 1),
                maxPerWeek = rObj.optInt("maxPerWeek", 2),
                maxPerShakhaPerDay = rObj.optInt("maxPerShakhaPerDay", 2),
                mustVisitAll = rObj.optBoolean("mustVisitAll", true),
                avoidDuplicate = rObj.optBoolean("avoidDuplicate", true),
                excludeSunday = rObj.optBoolean("excludeSunday", true),
                globalAvailableDays = gDays,
                weekStart = rObj.optInt("weekStart", 1)
            )
        } else {
            Rules()
        }

        val pObj = root.optJSONObject("period")
        val period = if (pObj != null) {
            Period(
                start = pObj.optString("start", DateHelper.defaultPeriod().start),
                end = pObj.optString("end", DateHelper.defaultPeriod().end)
            )
        } else {
            DateHelper.defaultPeriod()
        }

        val schedule = mutableListOf<PravasItem>()
        val schedArray = root.optJSONArray("schedule")
        if (schedArray != null) {
            for (i in 0 until schedArray.length()) {
                val obj = schedArray.getJSONObject(i)
                schedule.add(
                    PravasItem(
                        id = obj.optString("id", "$i"),
                        date = obj.optString("date", ""),
                        karyakartaId = obj.optInt("karyakartaId", 0),
                        karyakartaName = obj.optString("karyakartaName", ""),
                        shreni = obj.optString("shreni", ""),
                        shakha = obj.optString("shakha", ""),
                        kendriya = obj.optString("kendriya", "")
                    )
                )
            }
        }

        return AppData(
            karyakartas = if (karyakartas.isNotEmpty()) karyakartas else DEFAULT_KARYAKARTAS,
            shakhas = if (shakhas.isNotEmpty()) shakhas else DEFAULT_SHAKHAS,
            district = district,
            nagar = nagar,
            rules = rules,
            period = period,
            schedule = schedule
        )
    }
}
