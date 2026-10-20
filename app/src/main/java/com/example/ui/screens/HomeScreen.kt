package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.catalog.DiseaseCatalog
import com.example.data.model.DiseaseCondition
import com.example.data.model.OnboardingProfileData
import com.example.data.model.PatientProfile
import com.example.data.model.PracticeSession
import com.example.data.model.RecoveryLog
import com.example.data.model.TherapeuticPractice
import com.example.ui.components.TherapeuticHeroBannerIllustration

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    selectedCondition: DiseaseCondition,
    patientProfile: PatientProfile?,
    onboardingData: OnboardingProfileData,
    recoveryLogs: List<RecoveryLog>,
    practiceSessions: List<PracticeSession>,
    onStartPractice: (TherapeuticPractice) -> Unit,
    onNavigateToConditions: () -> Unit,
    onNavigateToPractice: () -> Unit,
    onNavigateToDiet: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToProfileAndClinician: () -> Unit,
    onSelectCondition: (String) -> Unit,
    onReopenOnboarding: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showEmergencySafetyDialog by rememberSaveable { mutableStateOf(false) }
    var showConsultClinicianDialog by rememberSaveable { mutableStateOf(false) }

    val latestLog = recoveryLogs.firstOrNull()
    val todaySessionsCount = practiceSessions.size.coerceAtLeast(if (latestLog != null) 1 else 0)
    val totalMinutesCompleted = recoveryLogs.sumOf { it.asanaMinutes + it.pranayamaMinutes + it.dhyanaMinutes } +
        practiceSessions.sumOf { it.durationMinutes }
    val streakDays = recoveryLogs.map { it.dateLabel }.distinct().size
    val dailySymptomScore = latestLog?.painLevel ?: (patientProfile?.baselinePainScore ?: 6)
    val sleepQualityFiveScale = ((latestLog?.sleepQuality ?: 8) / 2).coerceIn(1, 5)
    val routineAdherence = latestLog?.dietAdherenceScore ?: 85

    val primaryAsana = selectedCondition.asanas.firstOrNull()
    val primaryPranayama = selectedCondition.pranayamas.firstOrNull()
    val primaryDhyana = selectedCondition.dhyanas.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Card: Start Today's Yoga Session
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
            ) {
                TherapeuticHeroBannerIllustration(
                    accentColor = Color(selectedCondition.accentHex),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(205.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "SUPPORTIVE YOGA THERAPY • ${selectedCondition.shortName.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFBF8F3),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Start Today's Yoga Session",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFFBF8F3),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Conservative ${onboardingData.availablePracticeMinutes}-min routine tailored for ${selectedCondition.name} (${onboardingData.mobilityLevel}).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE6EFEA)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                primaryAsana?.let { onStartPractice(it) } ?: onNavigateToPractice()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFBF8F3),
                                contentColor = Color(0xFF143325)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("home_start_today_session_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Begin Guided Routine",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = { showEmergencySafetyDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFFCE6DC)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("home_safety_triage_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Safety Check")
                        }
                    }
                }
            }
        }

        // 2. Daily Activity & Clinical Summary Dashboard (Adherence Separated from Symptom Score)
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Activity & Symptom Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onNavigateToProgress) {
                            Text("Update Check-In")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SummaryStatTile(
                            label = "Sessions",
                            value = "$todaySessionsCount done",
                            subtext = "${totalMinutesCompleted}m total",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryStatTile(
                            label = "Streak",
                            value = "$streakDays days",
                            subtext = "Adherence $routineAdherence%",
                            color = Color(0xFFC98A2E),
                            modifier = Modifier.weight(1f)
                        )
                        SummaryStatTile(
                            label = "Symptom",
                            value = "$dailySymptomScore/10",
                            subtext = "Sleep $sleepQualityFiveScale/5 ★",
                            color = Color(0xFFB85D3B),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Note: Practice streak reflects habit consistency and is tracked separately from clinical symptom changes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 3. My Personalized Daily Yoga Plan (Morning / Afternoon / Evening / Night)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "My Personalized Daily Plan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "${onboardingData.age} yrs • ${onboardingData.ageGroup}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Primary Goal: ${onboardingData.primaryHealthGoal}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    if (onboardingData.selectedSymptoms.isNotEmpty()) {
                        Text(
                            text = "Target Symptoms: ${onboardingData.selectedSymptoms.joinToString(" • ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(
                        onClick = onReopenOnboarding,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.testTag("home_reopen_onboarding_button")
                    ) {
                        Text("Customize Age, Goal & Symptoms")
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    DailyPlanRow(
                        slot = "Morning (10–15 min)",
                        title = primaryAsana?.title ?: "Gentle Joint & Spinal Mobility",
                        subtitle = "Conservative Asana with Chair/Wall support option",
                        onClick = { primaryAsana?.let(onStartPractice) ?: onNavigateToPractice() }
                    )
                    DailyPlanRow(
                        slot = "Afternoon (3–5 min)",
                        title = "Ergonomic Posture & Scapular Micro-Break",
                        subtitle = "Unloads prolonged sitting & screen tension",
                        onClick = onNavigateToPractice
                    )
                    DailyPlanRow(
                        slot = "Evening (5–10 min)",
                        title = primaryPranayama?.title ?: "Screened Slow Diaphragmatic Breathing",
                        subtitle = "Zero-retention autonomic calming breathwork",
                        onClick = { primaryPranayama?.let(onStartPractice) ?: onNavigateToPractice() }
                    )
                    DailyPlanRow(
                        slot = "Night (3–5 min)",
                        title = primaryDhyana?.title ?: "Sleep Preparation & Symptom Check-In",
                        subtitle = "Body-scan relaxation & <2 min recovery log",
                        onClick = onNavigateToProgress
                    )
                }
            }
        }

        // 4. Explore Conditions Quick Carousel (All 17+ Conditions)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Explore Conditions (${DiseaseCatalog.conditions.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onNavigateToConditions) {
                        Text("View Full Library")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DiseaseCatalog.conditions.forEach { cond ->
                        val isSelected = cond.id == selectedCondition.id
                        Surface(
                            modifier = Modifier
                                .width(195.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    onSelectCondition(cond.id)
                                    onNavigateToConditions()
                                },
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(18.dp),
                            tonalElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = cond.categoryTag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                    else
                                        MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = cond.shortName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${cond.asanas.size} Asanas • ${cond.pranayamas.size} Pranayama",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Core Modules Grid (Pranayama, Meditation, Diet, Recovery, Clinician, Emergency)
        item {
            Text(
                text = "Therapeutic & Clinical Modules",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleQuickCard(
                        title = "Pranayama & Breathing",
                        subtitle = "Visual pacer • Zero forced retention",
                        icon = Icons.Default.Air,
                        accent = Color(0xFF1F4E38),
                        onClick = onNavigateToPractice,
                        modifier = Modifier.weight(1f)
                    )
                    ModuleQuickCard(
                        title = "Meditation & Wellness",
                        subtitle = "3, 5, 10 & 15 min guided Dhyan",
                        icon = Icons.Default.Psychology,
                        accent = Color(0xFF284B5A),
                        onClick = onNavigateToPractice,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleQuickCard(
                        title = "Diet & Daily Routine",
                        subtitle = "Indian Veg/Non-Veg & Regional plans",
                        icon = Icons.Default.RestaurantMenu,
                        accent = Color(0xFFC98A2E),
                        onClick = onNavigateToDiet,
                        modifier = Modifier.weight(1f)
                    )
                    ModuleQuickCard(
                        title = "My Recovery Progress",
                        subtitle = "Symptom charts & PDF Export",
                        icon = Icons.Default.Insights,
                        accent = Color(0xFF347456),
                        onClick = onNavigateToProgress,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleQuickCard(
                        title = "Consult a Professional",
                        subtitle = "Clinician dashboard & review notes",
                        icon = Icons.Default.LocalHospital,
                        accent = Color(0xFF3A6B56),
                        onClick = { showConsultClinicianDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                    ModuleQuickCard(
                        title = "Emergency & Safety",
                        subtitle = "Free red-flag triage & stop signals",
                        icon = Icons.Default.Emergency,
                        accent = Color(0xFFB85D3B),
                        onClick = { showEmergencySafetyDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    if (showEmergencySafetyDialog) {
        EmergencyAndSafetyModal(
            condition = selectedCondition,
            onDismiss = { showEmergencySafetyDialog = false }
        )
    }

    if (showConsultClinicianDialog) {
        ConsultProfessionalModal(
            condition = selectedCondition,
            onboardingData = onboardingData,
            onOpenClinicianPortal = {
                showConsultClinicianDialog = false
                onNavigateToProfileAndClinician()
            },
            onDismiss = { showConsultClinicianDialog = false }
        )
    }
}

@Composable
private fun SummaryStatTile(
    label: String,
    value: String,
    subtext: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DailyPlanRow(
    slot: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = slot,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Start",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ModuleQuickCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Surface(
                color = accent.copy(alpha = 0.14f),
                shape = CircleShape,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmergencyAndSafetyModal(
    condition: DiseaseCondition,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Emergency,
                    contentDescription = null,
                    tint = Color(0xFFB85D3B)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Emergency & Safety Triage (Always Free)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "STOP YOUR SESSION IMMEDIATELY AND SEEK URGENT MEDICAL CARE IF YOU EXPERIENCE:",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFFB85D3B),
                    fontWeight = FontWeight.Bold
                )
                val redFlags = listOf(
                    "New bladder or bowel dysfunction or saddle-area numbness with back pain (Cauda Equina red flag)",
                    "Rapidly progressive weakness, numbness, or electric shooting pain in an arm or leg",
                    "Chest pain, pressure, fainting (syncope), palpitations, or severe breathlessness",
                    "Sudden 'thunderclap' severe headache, visual loss, slurred speech, or facial droop",
                    "Suicidal thoughts or immediate risk of self-harm (Call national emergency 112 / Tele-MANAS 14416)"
                )
                redFlags.forEach { flag ->
                    Text(
                        text = "• $flag",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Condition-Specific Referral Criteria (${condition.shortName}):",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                condition.medicalReferralCriteria.forEach { item ->
                    Text(text = "• $item", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Disclaimer: YogaSwasthya provides supportive wellness education and never replaces medical diagnosis or emergency care.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("I Understand")
            }
        }
    )
}

@Composable
private fun ConsultProfessionalModal(
    condition: DiseaseCondition,
    onboardingData: OnboardingProfileData,
    onOpenClinicianPortal: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Consult a Qualified Professional",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "When to Consult an Orthopedic Specialist, Physiotherapist, Neurologist, or Certified Yoga Therapist:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• Before starting movement therapy if you have recent surgery, severe osteoporosis, pregnancy, or uncontrolled hypertension/diabetes.\n" +
                        "• If your pain or stiffness worsens over 7–14 days despite gentle modifications.\n" +
                        "• To review your self-reported YogaSwasthya PDF Progress Report during your clinic visit.",
                    style = MaterialTheme.typography.bodySmall
                )
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Current Clinician / Therapist Note:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = onboardingData.clinicianNotes,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Next Scheduled Review: ${onboardingData.nextClinicianReviewDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onOpenClinicianPortal) {
                Text("Open Clinician Dashboard")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
