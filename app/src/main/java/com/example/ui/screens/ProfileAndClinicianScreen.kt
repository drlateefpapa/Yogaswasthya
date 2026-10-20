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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Person
import com.example.data.catalog.DiseaseCatalog
import com.example.data.model.DiseaseCondition
import com.example.data.model.OnboardingProfileData
import com.example.data.model.PatientProfile
import com.example.data.model.RecoveryLog
import com.example.data.model.calculateBmi
import com.example.data.model.describeBmiCategory
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileAndClinicianScreen(
    selectedCondition: DiseaseCondition,
    patientProfile: PatientProfile?,
    onboardingData: OnboardingProfileData,
    recoveryLogs: List<RecoveryLog>,
    onUpdateOnboardingData: (OnboardingProfileData) -> Unit,
    onSelectCondition: (String) -> Unit,
    onDeleteAllLogs: () -> Unit,
    onSignOut: () -> Unit,
    onReopenOnboarding: () -> Unit = {},
    onExportWeeklyPdf: ((shareWithClinician: Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var clinicianModeUnlocked by rememberSaveable { mutableStateOf(false) }
    var showTriageModal by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirmModal by rememberSaveable { mutableStateOf(false) }
    var remindersEnabled by rememberSaveable { mutableStateOf(true) }
    var largeTextAccessibility by rememberSaveable { mutableStateOf(false) }
    var editableClinicianNotes by remember(onboardingData.clinicianNotes) {
        mutableStateOf(onboardingData.clinicianNotes)
    }

    val resolvedFullName = patientProfile?.displayName?.ifBlank { onboardingData.fullName } ?: onboardingData.fullName
    val resolvedAge = patientProfile?.age?.takeIf { it > 0 } ?: onboardingData.age
    val resolvedHeightCm = patientProfile?.heightCm?.takeIf { it > 0 } ?: onboardingData.heightCm
    val resolvedWeightKg = patientProfile?.weightKg?.takeIf { it > 0 } ?: onboardingData.weightKg
    val resolvedKnownDiseases = patientProfile?.knownDiseasesOrMedications?.ifBlank { onboardingData.knownDiseasesOrMedications }
        ?: onboardingData.knownDiseasesOrMedications
    val resolvedEmailId = patientProfile?.emailId?.ifBlank { onboardingData.emailId } ?: onboardingData.emailId
    val resolvedPhone = patientProfile?.phoneNumber?.ifBlank { onboardingData.phoneNumber } ?: onboardingData.phoneNumber

    var nameInput by remember(resolvedFullName) { mutableStateOf(resolvedFullName) }
    var ageInput by remember(resolvedAge) { mutableStateOf(resolvedAge.toString()) }
    var heightInput by remember(resolvedHeightCm) { mutableStateOf(resolvedHeightCm.toString()) }
    var weightInput by remember(resolvedWeightKg) { mutableStateOf(resolvedWeightKg.toString()) }
    var knownDiseaseInput by remember(resolvedKnownDiseases) { mutableStateOf(resolvedKnownDiseases) }
    var emailInput by remember(resolvedEmailId) { mutableStateOf(resolvedEmailId) }
    var phoneInput by remember(resolvedPhone) { mutableStateOf(resolvedPhone) }

    val parsedAge = ageInput.toIntOrNull()?.coerceIn(18, 95) ?: resolvedAge
    val parsedHeightCm = heightInput.toIntOrNull()?.coerceIn(50, 260) ?: resolvedHeightCm
    val parsedWeightKg = weightInput.toIntOrNull()?.coerceIn(15, 300) ?: resolvedWeightKg
    val calculatedBmi = remember(parsedHeightCm, parsedWeightKg) {
        calculateBmi(parsedHeightCm, parsedWeightKg)
    }
    val bmiCategory = remember(calculatedBmi) {
        describeBmiCategory(calculatedBmi)
    }

    val worseningAlert = recoveryLogs.size >= 2 &&
        (recoveryLogs.first().painLevel - recoveryLogs.last().painLevel >= 2 || recoveryLogs.first().painLevel >= 8)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_clinician_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 0. Patient Personal, Anthropometric & Medical Record Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Patient Personal & Medical Details",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Name, age, height, weight, known disease/medication, mail ID & phone no.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it.take(80) },
                        label = { Text("Patient Full Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_name_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = ageInput,
                            onValueChange = { raw ->
                                ageInput = raw.filter { it.isDigit() }.take(3)
                            },
                            label = { Text("Age (yrs)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_age_input")
                        )

                        OutlinedTextField(
                            value = heightInput,
                            onValueChange = { raw ->
                                heightInput = raw.filter { it.isDigit() }.take(3)
                            },
                            label = { Text("Height (cm)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_height_input")
                        )

                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { raw ->
                                weightInput = raw.filter { it.isDigit() }.take(3)
                            },
                            label = { Text("Weight (kg)") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_weight_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_bmi_summary_badge")
                    ) {
                        Text(
                            text = String.format(
                                Locale.US,
                                "Body Metrics: %d cm • %d kg • BMI %.1f kg/m² (%s)",
                                parsedHeightCm,
                                parsedWeightKg,
                                calculatedBmi,
                                bmiCategory
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = knownDiseaseInput,
                        onValueChange = { knownDiseaseInput = it.take(300) },
                        label = { Text("Known Disease or Medication") },
                        placeholder = { Text("e.g., Hypertension, Type 2 Diabetes, Vitamin D3") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_known_disease_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it.take(120) },
                            label = { Text("Mail ID (Email)") },
                            placeholder = { Text("name@example.com") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_email_input")
                        )

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { raw ->
                                phoneInput = raw.filter { it.isDigit() || it in "+- ()" }.take(25)
                            },
                            label = { Text("Phone No.") },
                            placeholder = { Text("+91 98765 43210") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_phone_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val updatedAgeGroup = deriveAgeGroupLabel(parsedAge)
                            onUpdateOnboardingData(
                                onboardingData.copy(
                                    fullName = nameInput.trim().ifBlank { "Practitioner" },
                                    age = parsedAge,
                                    ageGroup = updatedAgeGroup,
                                    heightCm = parsedHeightCm,
                                    weightKg = parsedWeightKg,
                                    knownDiseasesOrMedications = knownDiseaseInput.trim().ifBlank { "None Reported" },
                                    emailId = emailInput.trim(),
                                    phoneNumber = phoneInput.trim()
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_save_details_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Personal & Medical Details",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 1. Onboarding & Personalization Preferences
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessibilityNew,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Personalized Onboarding & Routine Profile",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        TextButton(
                            onClick = onReopenOnboarding,
                            modifier = Modifier.testTag("profile_reopen_onboarding_button")
                        ) {
                            Text("Edit Full Wizard")
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${nameInput.ifBlank { onboardingData.fullName }} • Age: $parsedAge yrs (${onboardingData.ageGroup}) • ${parsedHeightCm}cm / ${parsedWeightKg}kg",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Known Disease / Meds: ${knownDiseaseInput.ifBlank { "None Reported" }} • Goal: ${onboardingData.primaryHealthGoal}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (emailInput.isNotBlank() || phoneInput.isNotBlank()) {
                        Text(
                            text = "Contact: ${emailInput.ifBlank { "No Email" }} • ${phoneInput.ifBlank { "No Phone" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (onboardingData.selectedSymptoms.isNotEmpty()) {
                        Text(
                            text = "Current Symptoms: ${onboardingData.selectedSymptoms.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Age Group", style = MaterialTheme.typography.labelLarge)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("18–34 yrs", "35–50 yrs", "51–65 yrs", "65+ Senior").forEach { age ->
                            FilterChip(
                                selected = onboardingData.ageGroup == age,
                                onClick = { onUpdateOnboardingData(onboardingData.copy(ageGroup = age)) },
                                label = { Text(age) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Mobility & Activity Tolerance", style = MaterialTheme.typography.labelLarge)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "Chair / Wall Support Needed",
                            "Moderate (Occasional Stiffness)",
                            "Active Preventive"
                        ).forEach { mob ->
                            FilterChip(
                                selected = onboardingData.mobilityLevel == mob,
                                onClick = { onUpdateOnboardingData(onboardingData.copy(mobilityLevel = mob)) },
                                label = { Text(mob) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Preferred Language (Localization Ready)", style = MaterialTheme.typography.labelLarge)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("English", "हिन्दी (Hindi)", "தமிழ் (Tamil)").forEach { lang ->
                            FilterChip(
                                selected = onboardingData.languageCode == lang,
                                onClick = { onUpdateOnboardingData(onboardingData.copy(languageCode = lang)) },
                                label = { Text(lang) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Dietary & Regional Food Preference", style = MaterialTheme.typography.labelLarge)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Vegetarian", "Vegetarian + Egg", "Non-Vegetarian", "Vegan / Dairy-Free").forEach { diet ->
                            FilterChip(
                                selected = onboardingData.dietaryType == diet,
                                onClick = { onUpdateOnboardingData(onboardingData.copy(dietaryType = diet)) },
                                label = { Text(diet) }
                            )
                        }
                    }
                }
            }
        }

        // 2. Pre-Session Health Screening & Medical Triage Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
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
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Safety & Medical Triage Questionnaire",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            color = if (onboardingData.redFlagsDetected) Color(0xFFB85D3B) else MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = if (onboardingData.redFlagsDetected) "Clinical Review Advised" else "Screened",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Screens for recent surgery, severe osteoporosis, balance risks, pregnancy, uncontrolled cardiovascular conditions, and neurological red flags.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { showTriageModal = true },
                        modifier = Modifier.testTag("open_triage_questionnaire_button")
                    ) {
                        Text("Complete / Review Health Screening")
                    }
                }
            }
        }

        // 3. Optional Access-Controlled Clinician & Therapist Dashboard (Section 13)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Clinician & Yoga Therapist Dashboard",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Patient-controlled consent & prescription supervision",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = clinicianModeUnlocked,
                            onCheckedChange = { clinicianModeUnlocked = it },
                            modifier = Modifier.testTag("clinician_mode_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = onboardingData.clinicianSharingConsent,
                            onCheckedChange = {
                                onUpdateOnboardingData(onboardingData.copy(clinicianSharingConsent = it))
                            }
                        )
                        Text(
                            text = "I grant informed consent to share my self-reported progress logs with my authorized clinician/yoga therapist.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (onExportWeeklyPdf != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onExportWeeklyPdf(false) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("clinician_export_weekly_pdf_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Export Weekly PDF")
                            }
                            OutlinedButton(
                                onClick = { onExportWeeklyPdf(true) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("clinician_share_weekly_pdf_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share with Clinician")
                            }
                        }
                    }

                    if (clinicianModeUnlocked) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        if (worseningAlert) {
                            Surface(
                                color = Color(0xFFFCE6DC),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WarningAmber,
                                        contentDescription = null,
                                        tint = Color(0xFFB85D3B)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "CLINICIAN ALERT: Patient reported elevated/worsening pain score (${recoveryLogs.firstOrNull()?.painLevel}/10). Consider pausing load and reviewing modifications.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF3E1809),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Assign Condition-Specific Routine:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            DiseaseCatalog.conditions.take(8).forEach { cond ->
                                FilterChip(
                                    selected = cond.id == selectedCondition.id,
                                    onClick = { onSelectCondition(cond.id) },
                                    label = { Text(cond.shortName, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = editableClinicianNotes,
                            onValueChange = { editableClinicianNotes = it },
                            label = { Text("Clinician Restrictions, Precautions & Dosage Notes") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                onUpdateOnboardingData(
                                    onboardingData.copy(clinicianNotes = editableClinicianNotes)
                                )
                            }
                        ) {
                            Text("Save Clinical Prescription & Notes")
                        }
                    }
                }
            }
        }

        // 4. Gentle Habit Reminders & Accessibility Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gentle Reminders & Accessibility",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Daily Yoga, Breathing & Check-In Reminders (${onboardingData.reminderTime})", style = MaterialTheme.typography.bodyMedium)
                            Text("Supportive, non-shaming recovery prompts", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = remindersEnabled, onCheckedChange = { remindersEnabled = it })
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enhanced High-Readability Mode", style = MaterialTheme.typography.bodyMedium)
                            Text("Captions enabled on all guided modules; screen-reader labels active", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = largeTextAccessibility, onCheckedChange = { largeTextAccessibility = it })
                    }
                }
            }
        }

        // 5. Privacy, DPDP Compliance, Monetization Transparency & Data Control
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PrivacyTip,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Privacy, Compliance & Plan Transparency",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Sensitive Health Data Protection: Encrypted in transit and at rest via Firebase Firestore with strict per-user isolation.\n" +
                            "• Zero Ad-Tracking: Your health logs are never sold or shared with advertising networks.\n" +
                            "• Always-Free Safety Guarantee: Basic yoga sessions, emergency triage, and safety instructions are 100% free forever.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { showDeleteConfirmModal = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB85D3B))
                        ) {
                            Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Clear Recovery Data")
                        }
                        OutlinedButton(onClick = onSignOut) {
                            Text("Sign Out")
                        }
                    }
                }
            }
        }
    }

    if (showTriageModal) {
        SafetyScreeningQuestionnaireModal(
            onboardingData = onboardingData,
            onSave = { updated ->
                onUpdateOnboardingData(updated)
                showTriageModal = false
            },
            onDismiss = { showTriageModal = false }
        )
    }

    if (showDeleteConfirmModal) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmModal = false },
            title = { Text("Delete Stored Recovery Logs?") },
            text = { Text("You have full control over your personal health records. This will permanently delete your stored recovery check-ins.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAllLogs()
                        showDeleteConfirmModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB85D3B))
                ) {
                    Text("Delete My Logs")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SafetyScreeningQuestionnaireModal(
    onboardingData: OnboardingProfileData,
    onSave: (OnboardingProfileData) -> Unit,
    onDismiss: () -> Unit
) {
    var recentSurgery by remember { mutableStateOf(false) }
    var severeOsteoporosisOrBalance by remember { mutableStateOf(false) }
    var unexplainedNeurological by remember { mutableStateOf(false) }
    var uncontrolledCardioOrPregnancy by remember { mutableStateOf(false) }

    val anyFlag = recentSurgery || severeOsteoporosisOrBalance || unexplainedNeurological || uncontrolledCardioOrPregnancy

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pre-Session Health Screening & Triage") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Please check if any of the following apply to you today:",
                    style = MaterialTheme.typography.bodySmall
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = recentSurgery, onCheckedChange = { recentSurgery = it })
                    Text("Recent surgery, fracture, or acute severe injury", style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = severeOsteoporosisOrBalance, onCheckedChange = { severeOsteoporosisOrBalance = it })
                    Text("Severe osteoporosis, frequent falls, or dizziness", style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = unexplainedNeurological, onCheckedChange = { unexplainedNeurological = it })
                    Text("New limb weakness, spreading numbness, or bowel/bladder changes", style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = uncontrolledCardioOrPregnancy, onCheckedChange = { uncontrolledCardioOrPregnancy = it })
                    Text("Uncontrolled blood pressure, chest pain, or high-risk pregnancy", style = MaterialTheme.typography.bodySmall)
                }

                if (anyFlag) {
                    Surface(
                        color = Color(0xFFFCE6DC),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "MEDICAL TRIAGE ADVISORY: Because one or more risk factors were selected, YogaSwasthya has switched your plan to gentle Chair/Restorative mode and recommends clearance from your physician before performing active postures.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF3E1809),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        onboardingData.copy(
                            hasCompletedSafetyScreening = true,
                            redFlagsDetected = anyFlag,
                            mobilityLevel = if (anyFlag) "Chair / Wall Support Needed" else onboardingData.mobilityLevel
                        )
                    )
                }
            ) {
                Text("Save Screening Result")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
