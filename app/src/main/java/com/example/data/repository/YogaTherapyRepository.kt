package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.OnboardingProfileData
import com.example.data.model.PatientProfile
import com.example.data.model.PracticeSession
import com.example.data.model.RecoveryLog
import com.example.util.PdfReportGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class YogaTherapyRepository(
    context: Context? = null
) {
    private val prefs: SharedPreferences? = try {
        context?.applicationContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    } catch (_: Exception) {
        null
    }

    private val profilesFlow = MutableStateFlow<Map<String, PatientProfile>>(loadProfilesFromPrefs())
    private val logsFlow = MutableStateFlow<Map<String, List<RecoveryLog>>>(loadLogsFromPrefs())
    private val sessionsFlow = MutableStateFlow<Map<String, List<PracticeSession>>>(loadSessionsFromPrefs())

    fun observePatientProfile(userId: String): Flow<PatientProfile?> {
        val key = userId.ifBlank { DEFAULT_USER_ID }
        return profilesFlow.asStateFlow().map { map -> map[key] }
    }

    suspend fun getPatientProfile(userId: String): Result<PatientProfile?> {
        val key = userId.ifBlank { DEFAULT_USER_ID }
        return Result.success(profilesFlow.value[key])
    }

    suspend fun savePatientProfile(profile: PatientProfile): Result<Unit> {
        val key = profile.userId.ifBlank { profile.id.ifBlank { DEFAULT_USER_ID } }
        val normalized = profile.copy(
            id = key,
            userId = key,
            displayName = profile.displayName.ifBlank { "Yoga Practitioner" }.take(80),
            age = profile.age.coerceIn(1, 120),
            heightCm = profile.heightCm.coerceIn(50, 260),
            weightKg = profile.weightKg.coerceIn(15, 300),
            knownDiseasesOrMedications = profile.knownDiseasesOrMedications.take(300),
            emailId = profile.emailId.take(120),
            phoneNumber = profile.phoneNumber.take(30),
            updatedAtMillis = System.currentTimeMillis()
        )
        val updated = profilesFlow.value.toMutableMap().apply {
            put(key, normalized)
        }
        profilesFlow.value = updated
        saveProfilesToPrefs(updated)
        return Result.success(Unit)
    }

    fun observeRecoveryLogs(userId: String): Flow<List<RecoveryLog>> {
        val key = userId.ifBlank { DEFAULT_USER_ID }
        ensureSeedDataForUser(key)
        return logsFlow.asStateFlow().map { map ->
            (map[key] ?: emptyList()).sortedByDescending { it.createdAtMillis }
        }
    }

    suspend fun getRecoveryLogs(userId: String): Result<List<RecoveryLog>> {
        val key = userId.ifBlank { DEFAULT_USER_ID }
        ensureSeedDataForUser(key)
        val logs = (logsFlow.value[key] ?: emptyList()).sortedByDescending { it.createdAtMillis }
        return Result.success(logs)
    }

    suspend fun addRecoveryLog(log: RecoveryLog): Result<String> {
        val key = log.userId.ifBlank { DEFAULT_USER_ID }
        val newId = log.id.ifBlank { "log_${UUID.randomUUID().toString().take(8)}" }
        val now = System.currentTimeMillis()
        val entry = log.copy(
            id = newId,
            userId = key,
            conditionId = log.conditionId.ifBlank { "cervical_lumbar_spondylitis" },
            dateLabel = log.dateLabel.ifBlank {
                SimpleDateFormat("yyyy-MM-dd", Locale.US).format( java.util.Date(now) )
            },
            painLevel = log.painLevel.coerceIn(0, 10),
            stiffnessLevel = log.stiffnessLevel.coerceIn(0, 10),
            stressLevel = log.stressLevel.coerceIn(0, 10),
            mobilityScore = log.mobilityScore.coerceIn(1, 10),
            sleepQuality = log.sleepQuality.coerceIn(1, 10),
            createdAtMillis = now,
            updatedAtMillis = now
        )
        val currentList = (logsFlow.value[key] ?: emptyList()).toMutableList()
        currentList.add(0, entry)
        val updatedMap = logsFlow.value.toMutableMap().apply {
            put(key, currentList)
        }
        logsFlow.value = updatedMap
        saveLogsToPrefs(updatedMap)
        return Result.success(newId)
    }

    suspend fun deleteRecoveryLog(userId: String, logId: String): Result<Unit> {
        val key = userId.ifBlank { DEFAULT_USER_ID }
        val currentList = (logsFlow.value[key] ?: emptyList()).filterNot { it.id == logId }
        val updatedMap = logsFlow.value.toMutableMap().apply {
            put(key, currentList)
        }
        logsFlow.value = updatedMap
        saveLogsToPrefs(updatedMap)
        return Result.success(Unit)
    }

    fun observePracticeSessions(userId: String): Flow<List<PracticeSession>> {
        val key = userId.ifBlank { DEFAULT_USER_ID }
        ensureSeedDataForUser(key)
        return sessionsFlow.asStateFlow().map { map ->
            (map[key] ?: emptyList()).sortedByDescending { it.createdAtMillis }
        }
    }

    suspend fun getPracticeSessions(userId: String): Result<List<PracticeSession>> {
        val key = userId.ifBlank { DEFAULT_USER_ID }
        ensureSeedDataForUser(key)
        val sessions = (sessionsFlow.value[key] ?: emptyList()).sortedByDescending { it.createdAtMillis }
        return Result.success(sessions)
    }

    suspend fun logPracticeSession(session: PracticeSession): Result<String> {
        val key = session.userId.ifBlank { DEFAULT_USER_ID }
        val newId = session.id.ifBlank { "sess_${UUID.randomUUID().toString().take(8)}" }
        val now = System.currentTimeMillis()
        val entry = session.copy(
            id = newId,
            userId = key,
            conditionId = session.conditionId.ifBlank { "cervical_lumbar_spondylitis" },
            durationMinutes = session.durationMinutes.coerceIn(1, 180),
            reliefRating = session.reliefRating.coerceIn(1, 5),
            createdAtMillis = now,
            updatedAtMillis = now
        )
        val currentList = (sessionsFlow.value[key] ?: emptyList()).toMutableList()
        currentList.add(0, entry)
        val updatedMap = sessionsFlow.value.toMutableMap().apply {
            put(key, currentList)
        }
        sessionsFlow.value = updatedMap
        saveSessionsToPrefs(updatedMap)
        return Result.success(newId)
    }

    suspend fun exportWeeklyRecoveryProgressToPdf(
        context: Context,
        userId: String,
        conditionId: String,
        fallbackProfile: PatientProfile? = null,
        fallbackLogs: List<RecoveryLog> = emptyList(),
        fallbackSessions: List<PracticeSession> = emptyList(),
        onboardingData: OnboardingProfileData? = null
    ): Result<PdfReportGenerator.GeneratedReportSummary> {
        val key = userId.ifBlank { DEFAULT_USER_ID }
        val resolvedProfile = getPatientProfile(key).getOrNull() ?: fallbackProfile
        val storedLogs = getRecoveryLogs(key).getOrNull().orEmpty()
        val resolvedLogs = if (storedLogs.isNotEmpty()) storedLogs else fallbackLogs
        val storedSessions = getPracticeSessions(key).getOrNull().orEmpty()
        val resolvedSessions = if (storedSessions.isNotEmpty()) storedSessions else fallbackSessions

        val weeklyLogs = PdfReportGenerator.extractWeeklyLogs(resolvedLogs)
        val weeklySessions = PdfReportGenerator.extractWeeklySessions(resolvedSessions)

        return PdfReportGenerator.exportPdfReport(
            context = context,
            profile = resolvedProfile,
            conditionId = conditionId.ifBlank { resolvedProfile?.primaryConditionId ?: "osteoarthritis" },
            logs = weeklyLogs,
            sessions = weeklySessions,
            onboardingData = onboardingData
        )
    }

    private fun ensureSeedDataForUser(userId: String) {
        if (!logsFlow.value.containsKey(userId)) {
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val now = System.currentTimeMillis()
            val dayMs = 24L * 60L * 60L * 1000L
            val seededLogs = listOf(
                RecoveryLog(
                    id = "seed_log_1",
                    userId = userId,
                    conditionId = "cervical_lumbar_spondylitis",
                    dateLabel = fmt.format(java.util.Date(now - 3 * dayMs)),
                    painLevel = 6,
                    stiffnessLevel = 6,
                    stressLevel = 5,
                    mobilityScore = 5,
                    sleepQuality = 6,
                    asanaMinutes = 15,
                    pranayamaMinutes = 10,
                    dhyanaMinutes = 5,
                    completedPractices = listOf("Bhujangasana (Gentle Cobra)", "Nadi Shodhana"),
                    dietAdherenceScore = 80,
                    waterIntakeGlasses = 7,
                    notes = "Initial morning cervical stiffness; gentle spinal warm-up helped.",
                    createdAtMillis = now - 3 * dayMs
                ),
                RecoveryLog(
                    id = "seed_log_2",
                    userId = userId,
                    conditionId = "cervical_lumbar_spondylitis",
                    dateLabel = fmt.format(java.util.Date(now - 2 * dayMs)),
                    painLevel = 5,
                    stiffnessLevel = 4,
                    stressLevel = 4,
                    mobilityScore = 7,
                    sleepQuality = 7,
                    asanaMinutes = 20,
                    pranayamaMinutes = 10,
                    dhyanaMinutes = 10,
                    completedPractices = listOf("Marjaryasana-Bitilasana (Cat-Cow)", "Bhramari Pranayama"),
                    dietAdherenceScore = 85,
                    waterIntakeGlasses = 8,
                    notes = "Better neck rotation and calmer sleep after evening Bhramari.",
                    createdAtMillis = now - 2 * dayMs
                ),
                RecoveryLog(
                    id = "seed_log_3",
                    userId = userId,
                    conditionId = "cervical_lumbar_spondylitis",
                    dateLabel = fmt.format(java.util.Date(now - 1 * dayMs)),
                    painLevel = 3,
                    stiffnessLevel = 3,
                    stressLevel = 3,
                    mobilityScore = 8,
                    sleepQuality = 8,
                    asanaMinutes = 20,
                    pranayamaMinutes = 10,
                    dhyanaMinutes = 10,
                    completedPractices = listOf("Setu Bandhasana (Supported Bridge)", "Yoga Nidra Body Scan"),
                    dietAdherenceScore = 90,
                    waterIntakeGlasses = 9,
                    notes = "Noticeable reduction in lower-back tension during desk work.",
                    createdAtMillis = now - 1 * dayMs
                )
            )
            logsFlow.value = logsFlow.value.toMutableMap().apply { put(userId, seededLogs) }
        }
        if (!sessionsFlow.value.containsKey(userId)) {
            val now = System.currentTimeMillis()
            val seededSessions = listOf(
                PracticeSession(
                    id = "seed_sess_1",
                    userId = userId,
                    conditionId = "cervical_lumbar_spondylitis",
                    practiceId = "spondylitis_cat_cow",
                    practiceTitle = "Marjaryasana-Bitilasana (Gentle Cat-Cow)",
                    category = "Asana",
                    durationMinutes = 12,
                    reliefRating = 5,
                    createdAtMillis = now - 12 * 3600 * 1000L
                ),
                PracticeSession(
                    id = "seed_sess_2",
                    userId = userId,
                    conditionId = "cervical_lumbar_spondylitis",
                    practiceId = "spondylitis_bhramari",
                    practiceTitle = "Bhramari Pranayama (Humming Bee Breath)",
                    category = "Pranayama",
                    durationMinutes = 8,
                    reliefRating = 5,
                    createdAtMillis = now - 6 * 3600 * 1000L
                )
            )
            sessionsFlow.value = sessionsFlow.value.toMutableMap().apply { put(userId, seededSessions) }
        }
    }

    private fun loadProfilesFromPrefs(): Map<String, PatientProfile> {
        val raw = prefs?.getString(KEY_PROFILES, null) ?: return emptyMap()
        return runCatching {
            val obj = JSONObject(raw)
            val result = mutableMapOf<String, PatientProfile>()
            val keys = obj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                val item = obj.getJSONObject(k)
                val activeArr = item.optJSONArray("activeConditionIds")
                val activeList = mutableListOf<String>()
                if (activeArr != null) {
                    for (i in 0 until activeArr.length()) {
                        activeList.add(activeArr.optString(i))
                    }
                }
                result[k] = PatientProfile(
                    id = item.optString("id", k),
                    userId = item.optString("userId", k),
                    displayName = item.optString("displayName", "Practitioner"),
                    age = item.optInt("age", 42),
                    heightCm = item.optInt("heightCm", 165),
                    weightKg = item.optInt("weightKg", 68),
                    knownDiseasesOrMedications = item.optString("knownDiseasesOrMedications", "Cervical Spondylitis • Vitamin D3"),
                    emailId = item.optString("emailId", ""),
                    phoneNumber = item.optString("phoneNumber", ""),
                    primaryConditionId = item.optString("primaryConditionId", "cervical_lumbar_spondylitis"),
                    activeConditionIds = if (activeList.isNotEmpty()) activeList else listOf("cervical_lumbar_spondylitis"),
                    baselinePainScore = item.optInt("baselinePainScore", 6),
                    baselineMobilityScore = item.optInt("baselineMobilityScore", 5),
                    baselineStressScore = item.optInt("baselineStressScore", 6),
                    dietaryPreference = item.optString("dietaryPreference", "Vegetarian • Pan-Indian Sattvic"),
                    dailyGoalMinutes = item.optInt("dailyGoalMinutes", 30),
                    createdAtMillis = item.optLong("createdAtMillis", System.currentTimeMillis()),
                    updatedAtMillis = item.optLong("updatedAtMillis", System.currentTimeMillis())
                )
            }
            result
        }.getOrDefault(emptyMap())
    }

    private fun saveProfilesToPrefs(map: Map<String, PatientProfile>) {
        val p = prefs ?: return
        runCatching {
            val root = JSONObject()
            map.forEach { (k, profile) ->
                val item = JSONObject().apply {
                    put("id", profile.id)
                    put("userId", profile.userId)
                    put("displayName", profile.displayName)
                    put("age", profile.age)
                    put("heightCm", profile.heightCm)
                    put("weightKg", profile.weightKg)
                    put("knownDiseasesOrMedications", profile.knownDiseasesOrMedications)
                    put("emailId", profile.emailId)
                    put("phoneNumber", profile.phoneNumber)
                    put("primaryConditionId", profile.primaryConditionId)
                    put("activeConditionIds", JSONArray(profile.activeConditionIds))
                    put("baselinePainScore", profile.baselinePainScore)
                    put("baselineMobilityScore", profile.baselineMobilityScore)
                    put("baselineStressScore", profile.baselineStressScore)
                    put("dietaryPreference", profile.dietaryPreference)
                    put("dailyGoalMinutes", profile.dailyGoalMinutes)
                    put("createdAtMillis", profile.createdAtMillis)
                    put("updatedAtMillis", profile.updatedAtMillis)
                }
                root.put(k, item)
            }
            p.edit().putString(KEY_PROFILES, root.toString()).apply()
        }
    }

    private fun loadLogsFromPrefs(): Map<String, List<RecoveryLog>> {
        val raw = prefs?.getString(KEY_LOGS, null) ?: return emptyMap()
        return runCatching {
            val root = JSONObject(raw)
            val result = mutableMapOf<String, List<RecoveryLog>>()
            val keys = root.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                val arr = root.getJSONArray(k)
                val list = mutableListOf<RecoveryLog>()
                for (i in 0 until arr.length()) {
                    val item = arr.getJSONObject(i)
                    val practicesArr = item.optJSONArray("completedPractices")
                    val practices = mutableListOf<String>()
                    if (practicesArr != null) {
                        for (j in 0 until practicesArr.length()) {
                            practices.add(practicesArr.optString(j))
                        }
                    }
                    list.add(
                        RecoveryLog(
                            id = item.optString("id"),
                            userId = item.optString("userId", k),
                            conditionId = item.optString("conditionId", "cervical_lumbar_spondylitis"),
                            dateLabel = item.optString("dateLabel", ""),
                            painLevel = item.optInt("painLevel", 5),
                            stiffnessLevel = item.optInt("stiffnessLevel", 5),
                            stressLevel = item.optInt("stressLevel", 5),
                            mobilityScore = item.optInt("mobilityScore", 6),
                            sleepQuality = item.optInt("sleepQuality", 7),
                            asanaMinutes = item.optInt("asanaMinutes", 15),
                            pranayamaMinutes = item.optInt("pranayamaMinutes", 10),
                            dhyanaMinutes = item.optInt("dhyanaMinutes", 10),
                            completedPractices = practices,
                            dietAdherenceScore = item.optInt("dietAdherenceScore", 85),
                            waterIntakeGlasses = item.optInt("waterIntakeGlasses", 8),
                            notes = item.optString("notes").takeIf { it.isNotBlank() },
                            createdAtMillis = item.optLong("createdAtMillis", System.currentTimeMillis()),
                            updatedAtMillis = item.optLong("updatedAtMillis", System.currentTimeMillis())
                        )
                    )
                }
                result[k] = list
            }
            result
        }.getOrDefault(emptyMap())
    }

    private fun saveLogsToPrefs(map: Map<String, List<RecoveryLog>>) {
        val p = prefs ?: return
        runCatching {
            val root = JSONObject()
            map.forEach { (k, list) ->
                val arr = JSONArray()
                list.forEach { log ->
                    val item = JSONObject().apply {
                        put("id", log.id)
                        put("userId", log.userId)
                        put("conditionId", log.conditionId)
                        put("dateLabel", log.dateLabel)
                        put("painLevel", log.painLevel)
                        put("stiffnessLevel", log.stiffnessLevel)
                        put("stressLevel", log.stressLevel)
                        put("mobilityScore", log.mobilityScore)
                        put("sleepQuality", log.sleepQuality)
                        put("asanaMinutes", log.asanaMinutes)
                        put("pranayamaMinutes", log.pranayamaMinutes)
                        put("dhyanaMinutes", log.dhyanaMinutes)
                        put("completedPractices", JSONArray(log.completedPractices))
                        put("dietAdherenceScore", log.dietAdherenceScore)
                        put("waterIntakeGlasses", log.waterIntakeGlasses)
                        if (!log.notes.isNullOrBlank()) put("notes", log.notes)
                        put("createdAtMillis", log.createdAtMillis)
                        put("updatedAtMillis", log.updatedAtMillis)
                    }
                    arr.put(item)
                }
                root.put(k, arr)
            }
            p.edit().putString(KEY_LOGS, root.toString()).apply()
        }
    }

    private fun loadSessionsFromPrefs(): Map<String, List<PracticeSession>> {
        val raw = prefs?.getString(KEY_SESSIONS, null) ?: return emptyMap()
        return runCatching {
            val root = JSONObject(raw)
            val result = mutableMapOf<String, List<PracticeSession>>()
            val keys = root.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                val arr = root.getJSONArray(k)
                val list = mutableListOf<PracticeSession>()
                for (i in 0 until arr.length()) {
                    val item = arr.getJSONObject(i)
                    list.add(
                        PracticeSession(
                            id = item.optString("id"),
                            userId = item.optString("userId", k),
                            conditionId = item.optString("conditionId", "cervical_lumbar_spondylitis"),
                            practiceId = item.optString("practiceId"),
                            practiceTitle = item.optString("practiceTitle"),
                            category = item.optString("category", "Asana"),
                            durationMinutes = item.optInt("durationMinutes", 10),
                            reliefRating = item.optInt("reliefRating", 5),
                            createdAtMillis = item.optLong("createdAtMillis", System.currentTimeMillis()),
                            updatedAtMillis = item.optLong("updatedAtMillis", System.currentTimeMillis())
                        )
                    )
                }
                result[k] = list
            }
            result
        }.getOrDefault(emptyMap())
    }

    private fun saveSessionsToPrefs(map: Map<String, List<PracticeSession>>) {
        val p = prefs ?: return
        runCatching {
            val root = JSONObject()
            map.forEach { (k, list) ->
                val arr = JSONArray()
                list.forEach { s ->
                    val item = JSONObject().apply {
                        put("id", s.id)
                        put("userId", s.userId)
                        put("conditionId", s.conditionId)
                        put("practiceId", s.practiceId)
                        put("practiceTitle", s.practiceTitle)
                        put("category", s.category)
                        put("durationMinutes", s.durationMinutes)
                        put("reliefRating", s.reliefRating)
                        put("createdAtMillis", s.createdAtMillis)
                        put("updatedAtMillis", s.updatedAtMillis)
                    }
                    arr.put(item)
                }
                root.put(k, arr)
            }
            p.edit().putString(KEY_SESSIONS, root.toString()).apply()
        }
    }

    companion object {
        private const val PREFS_NAME = "yogaswasthya_local_store"
        private const val KEY_PROFILES = "patient_profiles_json"
        private const val KEY_LOGS = "recovery_logs_json"
        private const val KEY_SESSIONS = "practice_sessions_json"
        const val DEFAULT_USER_ID = "local_patient"
    }
}
