package com.example.util

import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.data.catalog.DiseaseCatalog
import com.example.data.model.OnboardingProfileData
import com.example.data.model.PatientProfile
import com.example.data.model.PracticeSession
import com.example.data.model.RecoveryLog
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN_X = 36f
    private const val CONTENT_WIDTH = PAGE_WIDTH - (MARGIN_X * 2)

    data class GeneratedReportSummary(
        val filePath: String,
        val generatedDate: String,
        val conditionName: String,
        val baselinePain: Int,
        val currentAvgPain: String,
        val baselineMobility: Int,
        val currentAvgMobility: String,
        val totalSessions: Int,
        val totalMinutes: Int,
        val adherenceRatePercent: Int,
        val sleepAndMoodSummary: String,
        val questionsForClinician: List<String>,
        val reportingWindowLabel: String = "",
        val currentAvgStiffness: String = "4.0 / 10",
        val currentAvgMood: String = "7.0 / 10",
        val weeklyDaysLogged: Int = 0,
        val weeklyAsanaMinutes: Int = 0,
        val weeklyPranayamaMinutes: Int = 0,
        val weeklyDhyanaMinutes: Int = 0,
        val avgDietAdherencePercent: Int = 85,
        val avgWaterGlasses: String = "8.0 glasses/day"
    )

    /**
     * Filters logs to the user's 7-day weekly recovery window (sorted newest first, up to 7 entries).
     */
    fun extractWeeklyLogs(logs: List<RecoveryLog>): List<RecoveryLog> {
        if (logs.isEmpty()) return emptyList()
        return logs
            .sortedByDescending { it.dateLabel.ifBlank { "0000-00-00" } }
            .take(7)
    }

    /**
     * Filters guided practice sessions to the most recent 7-day weekly window.
     */
    fun extractWeeklySessions(sessions: List<PracticeSession>): List<PracticeSession> {
        if (sessions.isEmpty()) return emptyList()
        val sevenDaysAgoMillis = System.currentTimeMillis() - (7L * 24L * 60L * 60L * 1000L)
        val filteredByTimestamp = sessions.filter { session ->
            val createdMillis = session.createdAtMillis
            createdMillis <= 0L || createdMillis >= sevenDaysAgoMillis
        }
        return (if (filteredByTimestamp.isNotEmpty()) filteredByTimestamp else sessions).take(14)
    }

    fun buildReportSummary(
        profile: PatientProfile?,
        conditionId: String,
        logs: List<RecoveryLog>,
        sessions: List<PracticeSession>
    ): GeneratedReportSummary {
        val condition = DiseaseCatalog.getConditionById(conditionId)
        val baselinePain = profile?.baselinePainScore ?: 7
        val baselineMobility = profile?.baselineMobilityScore ?: 4
        val weeklyLogs = extractWeeklyLogs(logs)
        val weeklySessions = extractWeeklySessions(sessions)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val now = Date()
        val cal = Calendar.getInstance().apply {
            time = now
            add(Calendar.DAY_OF_YEAR, -6)
        }
        val windowStart = weeklyLogs.minOfOrNull { it.dateLabel.ifBlank { dateFormat.format(cal.time) } }
            ?: dateFormat.format(cal.time)
        val windowEnd = weeklyLogs.maxOfOrNull { it.dateLabel.ifBlank { dateFormat.format(now) } }
            ?: dateFormat.format(now)
        val reportingWindow = "$windowStart to $windowEnd (7-Day Weekly Progress)"

        val avgPain = if (weeklyLogs.isNotEmpty()) {
            String.format(Locale.US, "%.1f / 10", weeklyLogs.map { it.painLevel }.average())
        } else {
            "$baselinePain.0 / 10 (Baseline)"
        }
        val avgStiffness = if (weeklyLogs.isNotEmpty()) {
            String.format(Locale.US, "%.1f / 10", weeklyLogs.map { it.stiffnessLevel }.average())
        } else {
            "4.0 / 10 (Baseline)"
        }
        val avgMood = if (weeklyLogs.isNotEmpty()) {
            String.format(
                Locale.US,
                "%.1f / 10",
                weeklyLogs.map { (10 - it.stressLevel).coerceIn(1, 10) }.average()
            )
        } else {
            "7.0 / 10"
        }
        val avgMob = if (weeklyLogs.isNotEmpty()) {
            String.format(Locale.US, "%.1f / 10", weeklyLogs.map { it.mobilityScore }.average())
        } else {
            "$baselineMobility.0 / 10 (Baseline)"
        }

        val weeklyAsanaMins = weeklyLogs.sumOf { it.asanaMinutes } +
            weeklySessions.filter { it.category.contains("Asana", ignoreCase = true) }.sumOf { it.durationMinutes }
        val weeklyPranayamaMins = weeklyLogs.sumOf { it.pranayamaMinutes } +
            weeklySessions.filter { it.category.contains("Pranayama", ignoreCase = true) }.sumOf { it.durationMinutes }
        val weeklyDhyanaMins = weeklyLogs.sumOf { it.dhyanaMinutes } +
            weeklySessions.filter {
                it.category.contains("Dhyana", ignoreCase = true) ||
                    it.category.contains("Meditation", ignoreCase = true)
            }.sumOf { it.durationMinutes }

        val totalMins = weeklyAsanaMins + weeklyPranayamaMins + weeklyDhyanaMins
        val distinctDaysLogged = weeklyLogs.map { it.dateLabel }.distinct().size.coerceAtLeast(weeklyLogs.size.coerceAtMost(7))
        val adherence = if (weeklyLogs.isNotEmpty()) {
            ((distinctDaysLogged.coerceAtMost(7) / 7f) * 100).toInt()
        } else 0

        val avgSleep = if (weeklyLogs.isNotEmpty()) {
            String.format(Locale.US, "%.1f / 10", weeklyLogs.map { it.sleepQuality }.average())
        } else "7.0 / 10"

        val avgStress = if (weeklyLogs.isNotEmpty()) {
            String.format(Locale.US, "%.1f/10", weeklyLogs.map { it.stressLevel }.average())
        } else "5.0/10"

        val avgDiet = if (weeklyLogs.isNotEmpty()) {
            weeklyLogs.map { it.dietAdherenceScore }.average().toInt()
        } else 85

        val avgWater = if (weeklyLogs.isNotEmpty()) {
            String.format(Locale.US, "%.1f glasses/day", weeklyLogs.map { it.waterIntakeGlasses }.average())
        } else "8.0 glasses/day"

        val questions = listOf(
            "1. Based on my self-reported 7-day pain trend ($avgPain vs baseline $baselinePain/10) and stiffness ($avgStiffness), should I progress or keep current modifications?",
            "2. Are there specific postures or weekly movement durations ($totalMins mins/wk) you recommend adjusting for ${condition.shortName}?",
            "3. How should I align my yoga, pranayama, and dietary routine ($avgDiet% adherence) with my current prescribed medical care?"
        )

        return GeneratedReportSummary(
            filePath = "",
            generatedDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(now),
            conditionName = condition.name,
            baselinePain = baselinePain,
            currentAvgPain = avgPain,
            baselineMobility = baselineMobility,
            currentAvgMobility = avgMob,
            totalSessions = weeklySessions.size + weeklyLogs.size,
            totalMinutes = totalMins,
            adherenceRatePercent = adherence,
            sleepAndMoodSummary = "Avg Sleep Quality: $avgSleep • Avg Mood: $avgMood • Avg Stress Score: $avgStress",
            questionsForClinician = questions,
            reportingWindowLabel = reportingWindow,
            currentAvgStiffness = avgStiffness,
            currentAvgMood = avgMood,
            weeklyDaysLogged = distinctDaysLogged,
            weeklyAsanaMinutes = weeklyAsanaMins,
            weeklyPranayamaMinutes = weeklyPranayamaMins,
            weeklyDhyanaMinutes = weeklyDhyanaMins,
            avgDietAdherencePercent = avgDiet,
            avgWaterGlasses = avgWater
        )
    }

    /**
     * Generates a structured, multi-section clinical PDF report of the user's weekly recovery progress
     * from Firestore models (`PatientProfile`, `RecoveryLog`, `PracticeSession`) and saves it to storage.
     */
    fun exportPdfReport(
        context: Context,
        profile: PatientProfile?,
        conditionId: String,
        logs: List<RecoveryLog>,
        sessions: List<PracticeSession>,
        onboardingData: OnboardingProfileData? = null
    ): Result<GeneratedReportSummary> {
        return try {
            val condition = DiseaseCatalog.getConditionById(conditionId)
            val weeklyLogs = extractWeeklyLogs(logs)
            val weeklySessions = extractWeeklySessions(sessions)
            val summary = buildReportSummary(profile, conditionId, weeklyLogs, weeklySessions)
            val patientName = profile?.displayName?.ifBlank {
                onboardingData?.fullName?.ifBlank { "Practitioner" } ?: "Practitioner"
            } ?: onboardingData?.fullName?.ifBlank { "Practitioner" } ?: "Practitioner"
            val resolvedAge = profile?.age?.takeIf { it > 0 } ?: onboardingData?.age ?: 42
            val resolvedHeight = profile?.heightCm?.takeIf { it > 0 } ?: onboardingData?.heightCm ?: 165
            val resolvedWeight = profile?.weightKg?.takeIf { it > 0 } ?: onboardingData?.weightKg ?: 68
            val resolvedKnownDisease = profile?.knownDiseasesOrMedications?.ifBlank {
                onboardingData?.knownDiseasesOrMedications.orEmpty()
            } ?: onboardingData?.knownDiseasesOrMedications.orEmpty()
            val resolvedContact = listOfNotNull(
                (profile?.emailId?.ifBlank { onboardingData?.emailId.orEmpty() })?.takeIf { it.isNotBlank() },
                (profile?.phoneNumber?.ifBlank { onboardingData?.phoneNumber.orEmpty() })?.takeIf { it.isNotBlank() }
            ).joinToString(" • ")

            val ageAndMobility = buildString {
                append("Age: ${resolvedAge}y • ${resolvedHeight}cm / ${resolvedWeight}kg")
                if (resolvedKnownDisease.isNotBlank()) {
                    append(" • Meds/Hx: ${resolvedKnownDisease.take(36)}")
                }
                if (resolvedContact.isNotBlank()) {
                    append(" • ${resolvedContact.take(34)}")
                }
            }

            val outDir = context.getExternalFilesDir(null) ?: context.filesDir
            if (!outDir.exists()) {
                outDir.mkdirs()
            }
            val pdfFile = File(outDir, "YogaSwasthya_Recovery_Report.pdf")

            runCatching {
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas: Canvas = page.canvas

            // Color Palette & Paints
            val headerBgPaint = Paint().apply {
                color = Color.rgb(22, 62, 45)
                style = Paint.Style.FILL
            }
            val cardBgPaint = Paint().apply {
                color = Color.rgb(245, 248, 245)
                style = Paint.Style.FILL
            }
            val cardStrokePaint = Paint().apply {
                color = Color.rgb(205, 218, 208)
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }
            val tableHeaderPaint = Paint().apply {
                color = Color.rgb(230, 238, 232)
                style = Paint.Style.FILL
            }
            val whiteTitlePaint = Paint().apply {
                color = Color.WHITE
                textSize = 15f
                isFakeBoldText = true
                isAntiAlias = true
            }
            val whiteSubPaint = Paint().apply {
                color = Color.rgb(225, 238, 230)
                textSize = 9.5f
                isAntiAlias = true
            }
            val sectionHeaderPaint = Paint().apply {
                color = Color.rgb(22, 62, 45)
                textSize = 11.5f
                isFakeBoldText = true
                isAntiAlias = true
            }
            val labelBoldPaint = Paint().apply {
                color = Color.rgb(30, 45, 38)
                textSize = 9.5f
                isFakeBoldText = true
                isAntiAlias = true
            }
            val bodyPaint = Paint().apply {
                color = Color.rgb(40, 52, 45)
                textSize = 9.5f
                isAntiAlias = true
            }
            val smallPaint = Paint().apply {
                color = Color.rgb(75, 88, 80)
                textSize = 8.5f
                isAntiAlias = true
            }
            val painLinePaint = Paint().apply {
                color = Color.rgb(184, 93, 59)
                style = Paint.Style.STROKE
                strokeWidth = 2f
                isAntiAlias = true
            }
            val mobilityLinePaint = Paint().apply {
                color = Color.rgb(31, 110, 74)
                style = Paint.Style.STROKE
                strokeWidth = 2f
                isAntiAlias = true
            }
            val disclaimerPaint = Paint().apply {
                color = Color.rgb(125, 65, 45)
                textSize = 8.5f
                isAntiAlias = true
            }

            // 1. TOP CLINICAL BANNER
            val bannerRect = RectF(MARGIN_X, 26f, MARGIN_X + CONTENT_WIDTH, 96f)
            canvas.drawRoundRect(bannerRect, 10f, 10f, headerBgPaint)

            canvas.drawText(
                "YOGASWASTHYA — WEEKLY CLINICAL RECOVERY & ADHERENCE REPORT",
                MARGIN_X + 14f,
                48f,
                whiteTitlePaint
            )
            canvas.drawText(
                "Patient: $patientName   |   Generated: ${summary.generatedDate}   |   Window: ${summary.reportingWindowLabel}",
                MARGIN_X + 14f,
                66f,
                whiteSubPaint
            )
            canvas.drawText(
                "Primary Condition: ${summary.conditionName}   |   $ageAndMobility",
                MARGIN_X + 14f,
                82f,
                whiteSubPaint
            )

            var y = 116f

            // 2. SECTION 1: 7-DAY CLINICAL OUTCOMES SUMMARY (SELF-REPORTED)
            canvas.drawText(
                "1. WEEKLY CLINICAL SYMPTOM OUTCOMES (PATIENT SELF-REPORTED)",
                MARGIN_X,
                y,
                sectionHeaderPaint
            )
            y += 8f

            val outcomeRect = RectF(MARGIN_X, y, MARGIN_X + CONTENT_WIDTH, y + 76f)
            canvas.drawRoundRect(outcomeRect, 8f, 8f, cardBgPaint)
            canvas.drawRoundRect(outcomeRect, 8f, 8f, cardStrokePaint)

            val colWidth = CONTENT_WIDTH / 3f
            // Row 1 inside Outcomes Box
            canvas.drawText(
                "Pain Score (0–10): ${summary.currentAvgPain}",
                MARGIN_X + 12f,
                y + 20f,
                labelBoldPaint
            )
            canvas.drawText(
                "Baseline: ${summary.baselinePain}/10 (Lower = Relief)",
                MARGIN_X + 12f,
                y + 34f,
                smallPaint
            )

            canvas.drawText(
                "Stiffness (0–10): ${summary.currentAvgStiffness}",
                MARGIN_X + colWidth + 8f,
                y + 20f,
                labelBoldPaint
            )
            canvas.drawText(
                "Morning / Joint Stiffness Avg",
                MARGIN_X + colWidth + 8f,
                y + 34f,
                smallPaint
            )

            canvas.drawText(
                "Mobility (1–10): ${summary.currentAvgMobility}",
                MARGIN_X + (colWidth * 2f) + 6f,
                y + 20f,
                labelBoldPaint
            )
            canvas.drawText(
                "Baseline: ${summary.baselineMobility}/10 (Higher = Better)",
                MARGIN_X + (colWidth * 2f) + 6f,
                y + 34f,
                smallPaint
            )

            // Row 2 inside Outcomes Box
            canvas.drawText(
                "Mood & Calm (1–10): ${summary.currentAvgMood}",
                MARGIN_X + 12f,
                y + 54f,
                labelBoldPaint
            )
            canvas.drawText(
                summary.sleepAndMoodSummary,
                MARGIN_X + colWidth + 8f,
                y + 54f,
                bodyPaint
            )
            if (!onboardingData?.clinicianNotes.isNullOrBlank()) {
                canvas.drawText(
                    "Clinician Note: ${onboardingData?.clinicianNotes?.take(75)}",
                    MARGIN_X + 12f,
                    y + 68f,
                    smallPaint
                )
            } else {
                canvas.drawText(
                    "Target Goal: ${onboardingData?.primaryHealthGoal ?: condition.programGoals.firstOrNull() ?: "Symptom & mobility support"}",
                    MARGIN_X + 12f,
                    y + 68f,
                    smallPaint
                )
            }

            y += 94f

            // 3. SECTION 2: WEEKLY ROUTINE ADHERENCE & YOGA PILLAR BREAKDOWN
            canvas.drawText(
                "2. WEEKLY PRACTICE ADHERENCE & PILLAR BREAKDOWN (TRACKED SEPARATELY FROM OUTCOMES)",
                MARGIN_X,
                y,
                sectionHeaderPaint
            )
            y += 8f

            val adherenceRect = RectF(MARGIN_X, y, MARGIN_X + CONTENT_WIDTH, y + 54f)
            canvas.drawRoundRect(adherenceRect, 8f, 8f, cardBgPaint)
            canvas.drawRoundRect(adherenceRect, 8f, 8f, cardStrokePaint)

            canvas.drawText(
                "• Weekly Check-In Adherence: ${summary.adherenceRatePercent}% (${summary.weeklyDaysLogged}/7 days logged)   |   Total Therapy Time: ${summary.totalMinutes} mins",
                MARGIN_X + 12f,
                y + 20f,
                labelBoldPaint
            )
            canvas.drawText(
                "• Pillar Breakdown: Asana ${summary.weeklyAsanaMinutes}m  •  Pranayama ${summary.weeklyPranayamaMinutes}m  •  Dhyana/Meditation ${summary.weeklyDhyanaMinutes}m",
                MARGIN_X + 12f,
                y + 35f,
                bodyPaint
            )
            canvas.drawText(
                "• Lifestyle & Sattvic Diet Adherence: ${summary.avgDietAdherencePercent}%   |   Hydration: ${summary.avgWaterGlasses}",
                MARGIN_X + 12f,
                y + 48f,
                smallPaint
            )

            y += 72f

            // 4. SECTION 3: 7-DAY TRAJECTORY MINI-CHART & STRUCTURED DAILY LOG TABLE
            canvas.drawText(
                "3. 7-DAY DAILY RECOVERY LOGS & SYMPTOM TRAJECTORY",
                MARGIN_X,
                y,
                sectionHeaderPaint
            )
            y += 8f

            // Draw visual mini trend chart if at least 2 logs exist
            val chronologicalLogs = weeklyLogs.sortedBy { it.dateLabel }
            if (chronologicalLogs.size >= 2) {
                val chartRect = RectF(MARGIN_X, y, MARGIN_X + CONTENT_WIDTH, y + 66f)
                canvas.drawRoundRect(chartRect, 6f, 6f, cardBgPaint)
                canvas.drawRoundRect(chartRect, 6f, 6f, cardStrokePaint)

                canvas.drawText(
                    "7-Day Trajectory (Terracotta = Pain 0–10, Forest Green = Mobility 1–10)",
                    MARGIN_X + 10f,
                    y + 14f,
                    smallPaint
                )

                val plotLeft = MARGIN_X + 24f
                val plotRight = MARGIN_X + CONTENT_WIDTH - 24f
                val plotTop = y + 22f
                val plotBottom = y + 56f
                val plotW = plotRight - plotLeft
                val plotH = plotBottom - plotTop

                val stepX = plotW / (chronologicalLogs.size - 1).coerceAtLeast(1)
                for (i in 0 until chronologicalLogs.size - 1) {
                    val x1 = plotLeft + i * stepX
                    val x2 = plotLeft + (i + 1) * stepX

                    val painY1 = plotBottom - (chronologicalLogs[i].painLevel.coerceIn(0, 10) / 10f) * plotH
                    val painY2 = plotBottom - (chronologicalLogs[i + 1].painLevel.coerceIn(0, 10) / 10f) * plotH
                    canvas.drawLine(x1, painY1, x2, painY2, painLinePaint)

                    val mobY1 = plotBottom - (chronologicalLogs[i].mobilityScore.coerceIn(0, 10) / 10f) * plotH
                    val mobY2 = plotBottom - (chronologicalLogs[i + 1].mobilityScore.coerceIn(0, 10) / 10f) * plotH
                    canvas.drawLine(x1, mobY1, x2, mobY2, mobilityLinePaint)
                }

                y += 74f
            }

            // Table Header
            val tableTop = y
            val headerRowRect = RectF(MARGIN_X, tableTop, MARGIN_X + CONTENT_WIDTH, tableTop + 18f)
            canvas.drawRect(headerRowRect, tableHeaderPaint)
            canvas.drawRect(headerRowRect, cardStrokePaint)

            canvas.drawText("Date", MARGIN_X + 6f, tableTop + 13f, labelBoldPaint)
            canvas.drawText("Pain", MARGIN_X + 74f, tableTop + 13f, labelBoldPaint)
            canvas.drawText("Stiff", MARGIN_X + 112f, tableTop + 13f, labelBoldPaint)
            canvas.drawText("Mood", MARGIN_X + 150f, tableTop + 13f, labelBoldPaint)
            canvas.drawText("Mobil", MARGIN_X + 190f, tableTop + 13f, labelBoldPaint)
            canvas.drawText("Sleep", MARGIN_X + 230f, tableTop + 13f, labelBoldPaint)
            canvas.drawText("Minutes (A/P/D)", MARGIN_X + 270f, tableTop + 13f, labelBoldPaint)
            canvas.drawText("Practices & Clinical Notes", MARGIN_X + 365f, tableTop + 13f, labelBoldPaint)

            y += 18f

            if (weeklyLogs.isEmpty()) {
                val emptyRowRect = RectF(MARGIN_X, y, MARGIN_X + CONTENT_WIDTH, y + 22f)
                canvas.drawRect(emptyRowRect, cardStrokePaint)
                canvas.drawText(
                    "Baseline established (Pain ${summary.baselinePain}/10, Mobility ${summary.baselineMobility}/10). Daily logs will populate this table.",
                    MARGIN_X + 8f,
                    y + 15f,
                    bodyPaint
                )
                y += 22f
            } else {
                weeklyLogs.take(7).forEach { log ->
                    val rowRect = RectF(MARGIN_X, y, MARGIN_X + CONTENT_WIDTH, y + 18f)
                    canvas.drawRect(rowRect, cardStrokePaint)
                    val moodVal = (10 - log.stressLevel).coerceIn(1, 10)
                    val noteOrPractices = when {
                        !log.notes.isNullOrBlank() -> log.notes.take(28)
                        log.completedPractices.isNotEmpty() -> log.completedPractices.joinToString(", ").take(28)
                        else -> "Routine completed"
                    }

                    canvas.drawText(log.dateLabel.take(10), MARGIN_X + 6f, y + 13f, bodyPaint)
                    canvas.drawText("${log.painLevel}/10", MARGIN_X + 74f, y + 13f, bodyPaint)
                    canvas.drawText("${log.stiffnessLevel}/10", MARGIN_X + 112f, y + 13f, bodyPaint)
                    canvas.drawText("$moodVal/10", MARGIN_X + 150f, y + 13f, bodyPaint)
                    canvas.drawText("${log.mobilityScore}/10", MARGIN_X + 190f, y + 13f, bodyPaint)
                    canvas.drawText("${log.sleepQuality}/10", MARGIN_X + 230f, y + 13f, bodyPaint)
                    canvas.drawText(
                        "${log.asanaMinutes}/${log.pranayamaMinutes}/${log.dhyanaMinutes}m",
                        MARGIN_X + 270f,
                        y + 13f,
                        bodyPaint
                    )
                    canvas.drawText(noteOrPractices, MARGIN_X + 365f, y + 13f, smallPaint)
                    y += 18f
                }
            }

            y += 16f

            // 5. SECTION 4: COMPLETED GUIDED SESSIONS & CONDITION PRECAUTIONS
            canvas.drawText(
                "4. COMPLETED GUIDED SESSIONS & CONDITION PRECAUTIONS",
                MARGIN_X,
                y,
                sectionHeaderPaint
            )
            y += 14f

            if (weeklySessions.isEmpty()) {
                canvas.drawText(
                    "• Guided Sessions: Tracked via daily check-in logs above.",
                    MARGIN_X + 6f,
                    y,
                    bodyPaint
                )
                y += 14f
            } else {
                weeklySessions.take(3).forEach { s ->
                    canvas.drawText(
                        "• ${s.practiceTitle} (${s.category} • ${s.durationMinutes} min) — Patient Relief Rating: ${s.reliefRating}/5 ★",
                        MARGIN_X + 6f,
                        y,
                        bodyPaint
                    )
                    y += 13f
                }
            }

            val topPrecaution = condition.aggravatingMovementsToAvoid.firstOrNull()
                ?: condition.precautionsSummary.ifBlank { "Avoid movements that provoke sharp, radiating, or escalating pain." }
            canvas.drawText(
                "• Key Precaution (${condition.shortName}): ${topPrecaution.take(92)}",
                MARGIN_X + 6f,
                y,
                smallPaint
            )
            y += 18f

            // 6. SECTION 5: QUESTIONS FOR CLINICIAN APPOINTMENT
            canvas.drawText(
                "5. QUESTIONS PREPARED FOR NEXT HEALTHCARE / PHYSIOTHERAPY APPOINTMENT",
                MARGIN_X,
                y,
                sectionHeaderPaint
            )
            y += 14f
            summary.questionsForClinician.forEach { q ->
                canvas.drawText(q.take(102), MARGIN_X + 6f, y, bodyPaint)
                y += 13f
            }

            // 7. FOOTER MEDICAL DISCLAIMER
            val footerTop = (PAGE_HEIGHT - 54).toFloat().coerceAtLeast(y + 8f)
            canvas.drawLine(MARGIN_X, footerTop - 6f, MARGIN_X + CONTENT_WIDTH, footerTop - 6f, cardStrokePaint)
            canvas.drawText(
                "MEDICAL DISCLAIMER: All metrics above are patient self-reported via YogaSwasthya. Yoga therapy is supportive care",
                MARGIN_X,
                footerTop + 6f,
                disclaimerPaint
            )
            canvas.drawText(
                "and does not replace medical diagnosis, physiotherapy, or pharmacological treatment. Please review with your clinician.",
                MARGIN_X,
                footerTop + 18f,
                disclaimerPaint
            )

                pdfDocument.finishPage(page)
                FileOutputStream(pdfFile).use { out ->
                    pdfDocument.writeTo(out)
                }
                pdfDocument.close()
            }

            if (!pdfFile.exists() || pdfFile.length() == 0L) {
                writeStructuredPdf14Fallback(
                    pdfFile = pdfFile,
                    summary = summary,
                    patientName = patientName,
                    ageAndMobility = ageAndMobility,
                    conditionName = condition.shortName,
                    weeklyLogs = weeklyLogs,
                    weeklySessions = weeklySessions
                )
            }

            saveCopyToPublicDownloads(context, pdfFile)

            Result.success(summary.copy(filePath = pdfFile.absolutePath))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveCopyToPublicDownloads(context: Context, sourcePdf: File) {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && sourcePdf.exists()) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "YogaSwasthya_Recovery_Report.pdf")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outStream ->
                        sourcePdf.inputStream().use { inStream ->
                            inStream.copyTo(outStream)
                        }
                    }
                }
            }
        }
    }

    private fun escapePdfText(input: String): String {
        return input
            .replace("\\", "\\\\")
            .replace("(", "\\(")
            .replace(")", "\\)")
            .replace("•", "-")
            .replace("–", "-")
            .replace("—", "-")
            .replace("★", "*")
    }

    private fun writeStructuredPdf14Fallback(
        pdfFile: File,
        summary: GeneratedReportSummary,
        patientName: String,
        ageAndMobility: String,
        conditionName: String,
        weeklyLogs: List<RecoveryLog>,
        weeklySessions: List<PracticeSession>
    ) {
        val lines = mutableListOf<String>()
        lines.add("YOGASWASTHYA - WEEKLY CLINICAL RECOVERY & ADHERENCE REPORT")
        lines.add("Patient: $patientName | Generated: ${summary.generatedDate}")
        lines.add("Reporting Window: ${summary.reportingWindowLabel}")
        lines.add("Primary Condition: ${summary.conditionName} ($ageAndMobility)")
        lines.add("--------------------------------------------------------------------------------")
        lines.add("1. WEEKLY CLINICAL SYMPTOM OUTCOMES (PATIENT SELF-REPORTED)")
        lines.add("   - 7-Day Pain Score: ${summary.currentAvgPain} (Baseline: ${summary.baselinePain}/10)")
        lines.add("   - 7-Day Stiffness Score: ${summary.currentAvgStiffness}")
        lines.add("   - 7-Day Mobility Score: ${summary.currentAvgMobility} (Baseline: ${summary.baselineMobility}/10)")
        lines.add("   - ${summary.sleepAndMoodSummary}")
        lines.add("--------------------------------------------------------------------------------")
        lines.add("2. WEEKLY PRACTICE ADHERENCE & PILLAR BREAKDOWN (TRACKED SEPARATELY)")
        lines.add("   - Check-In Adherence: ${summary.adherenceRatePercent}% (${summary.weeklyDaysLogged}/7 days logged)")
        lines.add("   - Total Therapy Time: ${summary.totalMinutes} mins (Asana ${summary.weeklyAsanaMinutes}m | Pranayama ${summary.weeklyPranayamaMinutes}m | Dhyana ${summary.weeklyDhyanaMinutes}m)")
        lines.add("   - Diet Adherence: ${summary.avgDietAdherencePercent}% | Hydration: ${summary.avgWaterGlasses}")
        lines.add("--------------------------------------------------------------------------------")
        lines.add("3. 7-DAY DAILY RECOVERY LOG TABLE")
        lines.add("   Date       | Pain | Stiff | Mood | Mobil | Sleep | Minutes (A/P/D)")
        if (weeklyLogs.isEmpty()) {
            lines.add("   Baseline established. Daily check-ins will appear here as recorded.")
        } else {
            weeklyLogs.take(7).forEach { log ->
                val mood = (10 - log.stressLevel).coerceIn(1, 10)
                lines.add(
                    "   ${log.dateLabel.take(10)} | ${log.painLevel}/10 | ${log.stiffnessLevel}/10  | $mood/10 | ${log.mobilityScore}/10  | ${log.sleepQuality}/10  | ${log.asanaMinutes}/${log.pranayamaMinutes}/${log.dhyanaMinutes}m"
                )
            }
        }
        if (weeklySessions.isNotEmpty()) {
            lines.add("--------------------------------------------------------------------------------")
            lines.add("4. COMPLETED GUIDED THERAPY SESSIONS ($conditionName)")
            weeklySessions.take(3).forEach { s ->
                lines.add("   - ${s.practiceTitle} (${s.category}, ${s.durationMinutes}m) - Relief: ${s.reliefRating}/5")
            }
        }
        lines.add("--------------------------------------------------------------------------------")
        lines.add("5. QUESTIONS FOR NEXT HEALTHCARE APPOINTMENT")
        summary.questionsForClinician.forEach { q ->
            lines.add("   ${q.take(95)}")
        }
        lines.add("--------------------------------------------------------------------------------")
        lines.add("DISCLAIMER: Patient self-reported supportive care log; does not replace medical diagnosis.")

        val contentBuilder = StringBuilder()
        contentBuilder.append("BT\n/F1 10 Tf\n36 800 Td\n14 TL\n")
        lines.forEachIndexed { index, rawLine ->
            val safe = escapePdfText(rawLine)
            if (index == 0) {
                contentBuilder.append("($safe) Tj\n")
            } else {
                contentBuilder.append("T* ($safe) Tj\n")
            }
        }
        contentBuilder.append("ET\n")
        val streamContent = contentBuilder.toString()

        val pdfText = buildString {
            append("%PDF-1.4\n")
            append("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n")
            append("2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n")
            append("3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>\nendobj\n")
            append("4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n")
            append("5 0 obj\n<< /Length ${streamContent.toByteArray(Charsets.US_ASCII).size} >>\nstream\n")
            append(streamContent)
            append("endstream\nendobj\n")
            append("trailer\n<< /Root 1 0 R >>\n%%EOF\n")
        }
        FileOutputStream(pdfFile).use { out ->
            out.write(pdfText.toByteArray(Charsets.US_ASCII))
        }
    }

    /**
     * Shares the exported PDF file with healthcare professionals via Android's system share sheet
     * using FileProvider (`application/pdf` stream + structured clinical summary text).
     * Falls back gracefully to text sharing if the PDF URI cannot be attached in a test environment.
     */
    fun sharePdfReport(
        context: Context,
        summary: GeneratedReportSummary,
        profile: PatientProfile? = null,
        conditionId: String = "osteoarthritis",
        logs: List<RecoveryLog> = emptyList(),
        sessions: List<PracticeSession> = emptyList(),
        onboardingData: OnboardingProfileData? = null
    ): Result<GeneratedReportSummary> {
        val resolvedSummary = if (summary.filePath.isNotBlank() && File(summary.filePath).exists()) {
            summary
        } else {
            val exportRes = exportPdfReport(
                context = context,
                profile = profile,
                conditionId = conditionId,
                logs = logs,
                sessions = sessions,
                onboardingData = onboardingData
            )
            exportRes.getOrElse { return Result.failure(it) }
        }

        val shareBody = buildShareSummaryText(resolvedSummary)
        val pdfFile = File(resolvedSummary.filePath)

        if (pdfFile.exists()) {
            try {
                val authority = "${context.packageName}.fileprovider"
                val pdfUri = FileProvider.getUriForFile(context, authority, pdfFile)
                val pdfIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_SUBJECT, "YogaSwasthya Weekly Recovery Report — ${resolvedSummary.conditionName}")
                    putExtra(Intent.EXTRA_TEXT, shareBody)
                    putExtra(Intent.EXTRA_STREAM, pdfUri)
                    clipData = ClipData.newRawUri("YogaSwasthya_Recovery_Report.pdf", pdfUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val chooser = Intent.createChooser(pdfIntent, "Share Weekly PDF Report with Healthcare Professional")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
                return Result.success(resolvedSummary)
            } catch (_: Exception) {
                // Fall back to text share if FileProvider is unavailable in unit test context
            }
        }

        shareReportText(context, resolvedSummary)
        return Result.success(resolvedSummary)
    }

    fun buildShareSummaryText(summary: GeneratedReportSummary): String {
        return buildString {
            appendLine("YOGASWASTHYA — Weekly Patient Recovery & Adherence Report")
            appendLine("Generated: ${summary.generatedDate}")
            if (summary.reportingWindowLabel.isNotBlank()) {
                appendLine("Reporting Window: ${summary.reportingWindowLabel}")
            }
            appendLine("Condition Focus: ${summary.conditionName}")
            appendLine("• 7-Day Pain Score: ${summary.currentAvgPain} (Baseline: ${summary.baselinePain}/10)")
            appendLine("• 7-Day Stiffness Score: ${summary.currentAvgStiffness}")
            appendLine("• 7-Day Mobility Score: ${summary.currentAvgMobility} (Baseline: ${summary.baselineMobility}/10)")
            appendLine("• ${summary.sleepAndMoodSummary}")
            appendLine(
                "• Weekly Routine Adherence (Tracked Separately): ${summary.adherenceRatePercent}% " +
                    "(${summary.weeklyDaysLogged}/7 days • ${summary.totalMinutes} total mins: " +
                    "Asana ${summary.weeklyAsanaMinutes}m, Pranayama ${summary.weeklyPranayamaMinutes}m, Dhyana ${summary.weeklyDhyanaMinutes}m)"
            )
            appendLine("\nQuestions for Next Clinician Review:")
            summary.questionsForClinician.forEach { appendLine(it) }
            appendLine("\nDisclaimer: Patient self-reported supportive care log; does not replace medical evaluation.")
        }
    }

    fun shareReportText(context: Context, summary: GeneratedReportSummary) {
        val shareBody = buildShareSummaryText(summary)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "YogaSwasthya Recovery Report — ${summary.conditionName}")
            putExtra(Intent.EXTRA_TEXT, shareBody)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(
                Intent.createChooser(intent, "Share Recovery Report with Clinician")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (_: Exception) {
        }
    }

    /**
     * Exports the patient's complete personal/medical profile and recovery check-in logs
     * to a CSV file in app storage + public Android Downloads folder, and optionally shares it.
     */
    fun exportPatientDataCsv(
        context: Context,
        profile: PatientProfile?,
        conditionId: String,
        logs: List<RecoveryLog>,
        sessions: List<PracticeSession>,
        onboardingData: OnboardingProfileData,
        shareFile: Boolean = true
    ): Result<String> {
        return try {
            val condition = DiseaseCatalog.getConditionById(conditionId)
            val fullName = profile?.displayName?.ifBlank { onboardingData.fullName } ?: onboardingData.fullName
            val age = profile?.age?.takeIf { it > 0 } ?: onboardingData.age
            val heightCm = profile?.heightCm?.takeIf { it > 0 } ?: onboardingData.heightCm
            val weightKg = profile?.weightKg?.takeIf { it > 0 } ?: onboardingData.weightKg
            val bmi = String.format(Locale.US, "%.1f", com.example.data.model.calculateBmi(heightCm, weightKg))
            val knownDiseases = profile?.knownDiseasesOrMedications?.ifBlank { onboardingData.knownDiseasesOrMedications }
                ?: onboardingData.knownDiseasesOrMedications
            val email = profile?.emailId?.ifBlank { onboardingData.emailId } ?: onboardingData.emailId
            val phone = profile?.phoneNumber?.ifBlank { onboardingData.phoneNumber } ?: onboardingData.phoneNumber

            val csvContent = buildString {
                appendLine("SECTION,FIELD,VALUE")
                appendLine("Patient Profile,Full Name,\"${fullName.replace("\"", "'")}\"")
                appendLine("Patient Profile,Age (Years),$age")
                appendLine("Patient Profile,Height (cm),$heightCm")
                appendLine("Patient Profile,Weight (kg),$weightKg")
                appendLine("Patient Profile,BMI (kg/m2),$bmi")
                appendLine("Patient Profile,Known Disease or Medication,\"${knownDiseases.replace("\"", "'")}\"")
                appendLine("Patient Profile,Mail ID (Email),\"${email.replace("\"", "'")}\"")
                appendLine("Patient Profile,Phone No.,\"${phone.replace("\"", "'")}\"")
                appendLine("Patient Profile,Primary Condition,\"${condition.name}\"")
                appendLine()
                appendLine("DATE,PAIN (0-10),STIFFNESS (0-10),STRESS (0-10),MOBILITY (1-10),SLEEP (1-10),ASANA MIN,PRANAYAMA MIN,DHYANA MIN,DIET %,WATER GLASSES,NOTES")
                logs.forEach { log ->
                    val safeNotes = (log.notes ?: "").replace("\"", "'")
                    appendLine(
                        "${log.dateLabel},${log.painLevel},${log.stiffnessLevel},${log.stressLevel}," +
                            "${log.mobilityScore},${log.sleepQuality},${log.asanaMinutes}," +
                            "${log.pranayamaMinutes},${log.dhyanaMinutes},${log.dietAdherenceScore}," +
                            "${log.waterIntakeGlasses},\"$safeNotes\""
                    )
                }
                if (sessions.isNotEmpty()) {
                    appendLine()
                    appendLine("SESSION TITLE,CATEGORY,DURATION MIN,RELIEF RATING (1-5)")
                    sessions.forEach { s ->
                        appendLine("\"${s.practiceTitle.replace("\"", "'")}\",${s.category},${s.durationMinutes},${s.reliefRating}")
                    }
                }
            }

            val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
            val csvFile = File(reportsDir, "YogaSwasthya_Patient_Export.csv")
            csvFile.writeText(csvContent, Charsets.UTF_8)

            runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val resolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, "YogaSwasthya_Patient_Export.csv")
                        put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    if (uri != null) {
                        resolver.openOutputStream(uri)?.use { out ->
                            out.write(csvContent.toByteArray(Charsets.UTF_8))
                        }
                    }
                }
            }

            if (shareFile) {
                runCatching {
                    val authority = "${context.packageName}.fileprovider"
                    val csvUri = FileProvider.getUriForFile(context, authority, csvFile)
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_SUBJECT, "YogaSwasthya Patient & Recovery Data Export — $fullName")
                        putExtra(Intent.EXTRA_TEXT, csvContent)
                        putExtra(Intent.EXTRA_STREAM, csvUri)
                        clipData = ClipData.newRawUri("YogaSwasthya_Patient_Export.csv", csvUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(
                        Intent.createChooser(sendIntent, "Export / Download Patient Health Record (CSV)")
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            }

            Result.success(csvFile.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

