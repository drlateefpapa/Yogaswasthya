package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.catalog.DiseaseCatalog
import com.example.data.model.DiseaseCondition
import com.example.data.model.PatientProfile
import com.example.data.model.PracticeSession
import com.example.data.model.RecoveryLog
import com.example.ui.components.PillarMinutesBar
import com.example.ui.components.RecoveryTrajectoryChart
import com.example.util.PdfReportGenerator

@Composable
fun RecoveryTrackerScreen(
    selectedCondition: DiseaseCondition,
    patientProfile: PatientProfile?,
    recoveryLogs: List<RecoveryLog>,
    practiceSessions: List<PracticeSession>,
    onSubmitDailyLog: (
        conditionId: String,
        painLevel: Int,
        stiffnessLevel: Int,
        stressLevel: Int,
        mobilityScore: Int,
        sleepQuality: Int,
        asanaMinutes: Int,
        pranayamaMinutes: Int,
        dhyanaMinutes: Int,
        completedPractices: List<String>,
        dietAdherenceScore: Int,
        waterIntakeGlasses: Int,
        notes: String
    ) -> Unit,
    onDeleteLog: (String) -> Unit,
    onExportStatusMessage: (String) -> Unit = {},
    onExportWeeklyPdf: ((shareWithClinician: Boolean, onExported: (PdfReportGenerator.GeneratedReportSummary) -> Unit) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showLogModal by rememberSaveable { mutableStateOf(false) }
    var lastExportedPath by rememberSaveable { mutableStateOf<String?>(null) }

    val baselinePain = patientProfile?.baselinePainScore ?: 7
    val baselineMobility = patientProfile?.baselineMobilityScore ?: 4
    val latestLog = recoveryLogs.firstOrNull()
    val currentPain = latestLog?.painLevel ?: baselinePain
    val currentMobility = latestLog?.mobilityScore ?: baselineMobility
    val painDelta = baselinePain - currentPain
    val mobilityDelta = currentMobility - baselineMobility

    val totalAsanaMins = recoveryLogs.sumOf { it.asanaMinutes } +
        practiceSessions.filter { it.category == "Asana" }.sumOf { it.durationMinutes }
    val totalPranayamaMins = recoveryLogs.sumOf { it.pranayamaMinutes } +
        practiceSessions.filter { it.category == "Pranayama" }.sumOf { it.durationMinutes }
    val totalDhyanaMins = recoveryLogs.sumOf { it.dhyanaMinutes } +
        practiceSessions.filter { it.category == "Dhyana" }.sumOf { it.durationMinutes }

    val avgDietAdherence = if (recoveryLogs.isNotEmpty()) {
        recoveryLogs.map { it.dietAdherenceScore }.average().toInt()
    } else 85

    val reportSummary = remember(patientProfile, selectedCondition.id, recoveryLogs, practiceSessions) {
        PdfReportGenerator.buildReportSummary(
            profile = patientProfile,
            conditionId = selectedCondition.id,
            logs = recoveryLogs,
            sessions = practiceSessions
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("recovery_tracker_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header & Log Today CTA (<2 Min Check-In)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Patient Recovery & PDF Reports",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Self-reported symptom trends & clinician-ready PDF export",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showLogModal = true },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("open_log_dialog_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log Today")
                }
            }
        }

        // 2. Recovery KPI Cards Row (Adherence separated from clinical outcomes)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiMetricCard(
                    title = "Pain Score",
                    value = "$currentPain/10",
                    subtitle = if (painDelta >= 0) "↓ $painDelta pts vs baseline ($baselinePain)" else "↑ ${-painDelta} pts vs baseline",
                    icon = Icons.Default.TrendingDown,
                    accentColor = Color(0xFFB85D3B),
                    modifier = Modifier.weight(1f)
                )
                KpiMetricCard(
                    title = "Mobility Score",
                    value = "$currentMobility/10",
                    subtitle = if (mobilityDelta >= 0) "↑ +$mobilityDelta pts vs baseline ($baselineMobility)" else "↓ $mobilityDelta pts",
                    icon = Icons.Default.TrendingUp,
                    accentColor = Color(0xFF1F4E38),
                    modifier = Modifier.weight(1f)
                )
                KpiMetricCard(
                    title = "Adherence",
                    value = "$avgDietAdherence%",
                    subtitle = "${recoveryLogs.size} check-ins logged",
                    icon = Icons.Default.Spa,
                    accentColor = Color(0xFFC98A2E),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 2.5 Interactive Daily Symptom (Pain, Stiffness, Mood Sliders) & Habit (Yoga, Pranayama, Meditation Checkboxes) Tracker
        item {
            DailySymptomAndHabitTrackerCard(
                selectedCondition = selectedCondition,
                initialPainLevel = currentPain,
                initialStiffnessLevel = latestLog?.stiffnessLevel ?: 4,
                initialMoodLevel = (10 - (latestLog?.stressLevel ?: 3)).coerceIn(1, 10),
                onSubmitDailyLog = onSubmitDailyLog
            )
        }

        // 3. Multi-Series Canvas Progress Chart Card (Pain, Mood, Stiffness, Mobility, Sleep over time)
        item {
            ProgressSymptomChartCard(
                selectedCondition = selectedCondition,
                patientProfile = patientProfile,
                recoveryLogs = recoveryLogs
            )
        }

        // 3.5 Cumulative Yoga Therapy Pillar Balance Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Cumulative Yoga Therapy Pillar Balance",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PillarMinutesBar(
                        asanaMins = totalAsanaMins.coerceAtLeast(15),
                        pranayamaMins = totalPranayamaMins.coerceAtLeast(10),
                        dhyanaMins = totalDhyanaMins.coerceAtLeast(10)
                    )
                }
            }
        }

        // 4. Clinician Summary & PDF Export Card (Section 12)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Clinician & Yoga Therapist Summary Report",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Structured PDF report for your doctor, physiotherapist, or yoga therapist",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "• Reporting Window: ${reportSummary.reportingWindowLabel}\n" +
                            "• Condition Focus: ${reportSummary.conditionName}\n" +
                            "• 7-Day Avg Pain: ${reportSummary.currentAvgPain} (Baseline: ${reportSummary.baselinePain}/10) • Avg Stiffness: ${reportSummary.currentAvgStiffness}\n" +
                            "• 7-Day Avg Mobility: ${reportSummary.currentAvgMobility} (Baseline: ${reportSummary.baselineMobility}/10)\n" +
                            "• ${reportSummary.sleepAndMoodSummary}\n" +
                            "• Weekly Routine Adherence (Reported Separately): ${reportSummary.adherenceRatePercent}% (${reportSummary.weeklyDaysLogged}/7 days • ${reportSummary.totalMinutes} mins: Asana ${reportSummary.weeklyAsanaMinutes}m, Prana ${reportSummary.weeklyPranayamaMinutes}m, Dhyana ${reportSummary.weeklyDhyanaMinutes}m)",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Questions Prepared for Your Next Appointment:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    reportSummary.questionsForClinician.forEach { q ->
                        Text(
                            text = q,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    if (lastExportedPath != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pdf_export_saved_banner")
                        ) {
                            Text(
                                text = "PDF Report Saved: $lastExportedPath",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (onExportWeeklyPdf != null) {
                                    onExportWeeklyPdf(false) { generated ->
                                        lastExportedPath = generated.filePath
                                    }
                                } else {
                                    val res = PdfReportGenerator.exportPdfReport(
                                        context = context,
                                        profile = patientProfile,
                                        conditionId = selectedCondition.id,
                                        logs = recoveryLogs,
                                        sessions = practiceSessions
                                    )
                                    res.onSuccess { generated ->
                                        lastExportedPath = generated.filePath
                                        onExportStatusMessage("Weekly PDF Recovery Report exported: YogaSwasthya_Recovery_Report.pdf")
                                    }.onFailure { err ->
                                        onExportStatusMessage(err.localizedMessage ?: "Could not generate PDF report")
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_pdf_report_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Weekly PDF")
                        }

                        OutlinedButton(
                            onClick = {
                                if (onExportWeeklyPdf != null) {
                                    onExportWeeklyPdf(true) { generated ->
                                        lastExportedPath = generated.filePath
                                    }
                                } else {
                                    val res = PdfReportGenerator.sharePdfReport(
                                        context = context,
                                        summary = reportSummary,
                                        profile = patientProfile,
                                        conditionId = selectedCondition.id,
                                        logs = recoveryLogs,
                                        sessions = practiceSessions
                                    )
                                    res.onSuccess { generated ->
                                        lastExportedPath = generated.filePath
                                        onExportStatusMessage("Weekly PDF Recovery Report ready to share with clinician")
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("share_clinician_report_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share PDF Report")
                        }
                    }
                }
            }
        }

        // 5. Recent Guided Practice Sessions
        if (practiceSessions.isNotEmpty()) {
            item {
                Text(
                    text = "Completed Guided Therapy Sessions (${practiceSessions.size})",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(practiceSessions.take(5), key = { it.id.ifBlank { it.practiceTitle } }) { session ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SelfImprovement,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = session.practiceTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${session.category} • ${session.durationMinutes} min • ${DiseaseCatalog.getConditionById(session.conditionId).shortName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "Relief ${session.reliefRating}/5 ★",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        // 6. Daily Recovery Check-In Logs History
        item {
            Text(
                text = "Daily Clinical Check-In Logs (${recoveryLogs.size})",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (recoveryLogs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Daily Recovery Check-Ins Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap 'Log Today' above to complete your <2 minute check-in (pain, stiffness, mobility, sleep, completed Asanas/Pranayama, and dietary adherence).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(recoveryLogs, key = { it.id.ifBlank { it.dateLabel } }) { log ->
                RecoveryLogItemCard(
                    log = log,
                    onDelete = { if (log.id.isNotBlank()) onDeleteLog(log.id) }
                )
            }
        }
    }

    if (showLogModal) {
        DailyRecoveryCheckInModal(
            selectedCondition = selectedCondition,
            onDismiss = { showLogModal = false },
            onSubmit = { condId, pain, stiff, stress, mob, sleep, asana, prana, dhyana, practices, dietScore, water, notes ->
                onSubmitDailyLog(
                    condId,
                    pain,
                    stiff,
                    stress,
                    mob,
                    sleep,
                    asana,
                    prana,
                    dhyana,
                    practices,
                    dietScore,
                    water,
                    notes
                )
                showLogModal = false
            }
        )
    }
}

@Composable
private fun KpiMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RecoveryLogItemCard(
    log: RecoveryLog,
    onDelete: () -> Unit
) {
    val cond = DiseaseCatalog.getConditionById(log.conditionId)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("recovery_log_card_${log.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${log.dateLabel} • ${cond.shortName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Asana ${log.asanaMinutes}m • Pranayama ${log.pranayamaMinutes}m • Dhyana ${log.dhyanaMinutes}m",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete log",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BadgePill("Pain: ${log.painLevel}/10", Color(0xFFB85D3B))
                BadgePill("Stiffness: ${log.stiffnessLevel}/10", Color(0xFFC98A2E))
                BadgePill("Mobility: ${log.mobilityScore}/10", Color(0xFF1F4E38))
                BadgePill("Diet: ${log.dietAdherenceScore}%", Color(0xFF347456))
            }

            if (!log.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${log.notes}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BadgePill(label: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.14f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DailyRecoveryCheckInModal(
    selectedCondition: DiseaseCondition,
    onDismiss: () -> Unit,
    onSubmit: (
        conditionId: String,
        painLevel: Int,
        stiffnessLevel: Int,
        stressLevel: Int,
        mobilityScore: Int,
        sleepQuality: Int,
        asanaMinutes: Int,
        pranayamaMinutes: Int,
        dhyanaMinutes: Int,
        completedPractices: List<String>,
        dietAdherenceScore: Int,
        waterIntakeGlasses: Int,
        notes: String
    ) -> Unit
) {
    var painLevel by remember { mutableIntStateOf(3) }
    var stiffnessLevel by remember { mutableIntStateOf(3) }
    var stressLevel by remember { mutableIntStateOf(3) }
    var mobilityScore by remember { mutableIntStateOf(7) }
    var sleepQuality by remember { mutableIntStateOf(8) }
    var headacheEpisode by remember { mutableStateOf(false) }
    var asanaMinutes by remember { mutableIntStateOf(20) }
    var pranayamaMinutes by remember { mutableIntStateOf(10) }
    var dhyanaMinutes by remember { mutableIntStateOf(10) }
    var dietAdherenceScore by remember { mutableIntStateOf(90) }
    var waterGlasses by remember { mutableIntStateOf(8) }
    var notes by remember { mutableStateOf("") }

    val allConditionPractices = remember(selectedCondition.id) {
        selectedCondition.asanas + selectedCondition.pranayamas + selectedCondition.dhyanas
    }
    val selectedPractices = remember(selectedCondition.id) {
        mutableStateListOf<String>().apply {
            allConditionPractices.take(2).forEach { add(it.title) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Quick Daily Recovery Check-In (<2 Min)",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Condition: ${selectedCondition.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Pain Level: $painLevel / 10 (Lower is better)",
                    style = MaterialTheme.typography.labelLarge
                )
                Slider(
                    value = painLevel.toFloat(),
                    onValueChange = { painLevel = it.toInt() },
                    valueRange = 0f..10f,
                    steps = 9
                )

                Text(
                    text = "Joint / Spine Stiffness: $stiffnessLevel / 10",
                    style = MaterialTheme.typography.labelLarge
                )
                Slider(
                    value = stiffnessLevel.toFloat(),
                    onValueChange = { stiffnessLevel = it.toInt() },
                    valueRange = 0f..10f,
                    steps = 9
                )

                Text(
                    text = "Functional Mobility & Daily Task Comfort: $mobilityScore / 10",
                    style = MaterialTheme.typography.labelLarge
                )
                Slider(
                    value = mobilityScore.toFloat(),
                    onValueChange = { mobilityScore = it.toInt() },
                    valueRange = 1f..10f,
                    steps = 8
                )

                Text(
                    text = "Stress, Anxiety & Fatigue Level: $stressLevel / 10",
                    style = MaterialTheme.typography.labelLarge
                )
                Slider(
                    value = stressLevel.toFloat(),
                    onValueChange = { stressLevel = it.toInt() },
                    valueRange = 0f..10f,
                    steps = 9
                )

                Text(
                    text = "Sleep Quality & Restorativeness: ${sleepQuality / 2} / 5 ★ ($sleepQuality/10)",
                    style = MaterialTheme.typography.labelLarge
                )
                Slider(
                    value = sleepQuality.toFloat(),
                    onValueChange = { sleepQuality = it.toInt() },
                    valueRange = 2f..10f,
                    steps = 7
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = headacheEpisode, onCheckedChange = { headacheEpisode = it })
                    Text(
                        text = "Experienced headache or migraine episode today",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = "Asana (${asanaMinutes}m) • Pranayama (${pranayamaMinutes}m) • Dhyana (${dhyanaMinutes}m)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = asanaMinutes.toString(),
                        onValueChange = { asanaMinutes = it.toIntOrNull()?.coerceIn(0, 240) ?: 0 },
                        label = { Text("Asana (m)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = pranayamaMinutes.toString(),
                        onValueChange = { pranayamaMinutes = it.toIntOrNull()?.coerceIn(0, 240) ?: 0 },
                        label = { Text("Prana (m)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = dhyanaMinutes.toString(),
                        onValueChange = { dhyanaMinutes = it.toIntOrNull()?.coerceIn(0, 240) ?: 0 },
                        label = { Text("Dhyana (m)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "Practices Completed Today",
                    style = MaterialTheme.typography.labelLarge
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allConditionPractices.forEach { practice ->
                        val isChosen = practice.title in selectedPractices
                        FilterChip(
                            selected = isChosen,
                            onClick = {
                                if (isChosen) selectedPractices.remove(practice.title)
                                else selectedPractices.add(practice.title)
                            },
                            label = {
                                Text(
                                    text = practice.title.substringBefore(" ("),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }
                }

                Text(
                    text = "Diet & Meal Timing Adherence: $dietAdherenceScore%",
                    style = MaterialTheme.typography.labelLarge
                )
                Slider(
                    value = dietAdherenceScore.toFloat(),
                    onValueChange = { dietAdherenceScore = (it.toInt() / 5) * 5 },
                    valueRange = 0f..100f,
                    steps = 19
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Warm Water Intake: $waterGlasses glasses",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Row {
                        TextButton(onClick = { if (waterGlasses > 0) waterGlasses -= 1 }) { Text("-") }
                        TextButton(onClick = { if (waterGlasses < 25) waterGlasses += 1 }) { Text("+") }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it.take(450) },
                    label = { Text("Symptom, Medication or Trigger Notes (Optional)") },
                    placeholder = { Text("e.g., Less morning stiffness, walked 20 mins comfortably") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recovery_notes_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val combinedNotes = buildString {
                        if (headacheEpisode) append("[Headache/Migraine Episode] ")
                        append(notes.trim())
                    }.trim()
                    onSubmit(
                        selectedCondition.id,
                        painLevel,
                        stiffnessLevel,
                        stressLevel,
                        mobilityScore,
                        sleepQuality,
                        asanaMinutes,
                        pranayamaMinutes,
                        dhyanaMinutes,
                        selectedPractices.toList(),
                        dietAdherenceScore,
                        waterGlasses,
                        combinedNotes
                    )
                },
                modifier = Modifier.testTag("submit_recovery_log_button")
            ) {
                Text("Save Check-In")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
