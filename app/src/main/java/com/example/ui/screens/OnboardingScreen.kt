package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.catalog.DiseaseCatalog
import com.example.data.model.OnboardingProfileData
import com.example.data.model.calculateBmi
import com.example.data.model.describeBmiCategory
import java.util.Locale

data class HealthGoalOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val recommendedConditionId: String
)

val PrimaryHealthGoals = listOf(
    HealthGoalOption(
        id = "pain_relief",
        title = "Joint & Spinal Pain Relief",
        subtitle = "Reduce neck, lower-back, knee, or hip discomfort safely",
        recommendedConditionId = "cervical_lumbar_spondylitis"
    ),
    HealthGoalOption(
        id = "mobility_posture",
        title = "Posture, Stiffness & Mobility Recovery",
        subtitle = "Restore range of motion for frozen shoulder, knee stiffness & desk posture",
        recommendedConditionId = "frozen_shoulder"
    ),
    HealthGoalOption(
        id = "anxiety_calm",
        title = "Anxiety, Stress & Nervous System Calm",
        subtitle = "Down-regulate sympathetic arousal with screened Pranayama & Dhyan",
        recommendedConditionId = "anxiety"
    ),
    HealthGoalOption(
        id = "sleep_fatigue",
        title = "Restorative Sleep & Fatigue Recovery",
        subtitle = "Unwind evening mental hyperarousal and improve sleep quality",
        recommendedConditionId = "sleep_difficulties"
    ),
    HealthGoalOption(
        id = "headache_migraine",
        title = "Headache & Migraine Prevention",
        subtitle = "Relieve suboccipital tension and neuro-vascular stress triggers",
        recommendedConditionId = "migraine"
    ),
    HealthGoalOption(
        id = "metabolic_bp",
        title = "Metabolic Health, Blood Sugar & BP Support",
        subtitle = "Adjunct movement & breath regulation for Diabetes, Obesity & Hypertension",
        recommendedConditionId = "type2_diabetes"
    ),
    HealthGoalOption(
        id = "preventive_stamina",
        title = "General Preventive Wellness & Flexibility",
        subtitle = "Rebuild daily movement stamina from a sedentary lifestyle",
        recommendedConditionId = "general_deconditioning"
    )
)

val CoreRoutineSymptoms = listOf(
    "Morning joint or spinal stiffness",
    "Neck or shoulder ache from prolonged sitting",
    "Lower-back pain or radiating leg discomfort",
    "Knee or hip discomfort during walking/stairs",
    "Elevated stress, anxiety, or difficulty relaxing",
    "Difficulty falling asleep or unrefreshing sleep",
    "Recurrent headache, neck tension, or visual fatigue",
    "Daytime fatigue or low physical stamina"
)

fun deriveAgeGroupLabel(age: Int): String = when {
    age < 35 -> "18–34 yrs"
    age <= 50 -> "35–50 yrs"
    age <= 64 -> "51–65 yrs"
    else -> "65+ Senior"
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    initialData: OnboardingProfileData,
    onCompleteOnboarding: (OnboardingProfileData) -> Unit,
    onSkipOnboarding: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var fullNameText by rememberSaveable { mutableStateOf(initialData.fullName) }
    var ageInputText by rememberSaveable { mutableStateOf(initialData.age.toString()) }
    var ageValue by rememberSaveable { mutableIntStateOf(initialData.age.coerceIn(18, 95)) }
    var selectedAgeGroup by rememberSaveable { mutableStateOf(deriveAgeGroupLabel(initialData.age)) }

    var heightInputText by rememberSaveable { mutableStateOf(initialData.heightCm.toString()) }
    var heightCmValue by rememberSaveable { mutableIntStateOf(initialData.heightCm.coerceIn(80, 240)) }
    var weightInputText by rememberSaveable { mutableStateOf(initialData.weightKg.toString()) }
    var weightKgValue by rememberSaveable { mutableIntStateOf(initialData.weightKg.coerceIn(25, 250)) }

    var knownDiseaseOrMedText by rememberSaveable { mutableStateOf(initialData.knownDiseasesOrMedications) }
    var emailIdText by rememberSaveable { mutableStateOf(initialData.emailId) }
    var phoneNumberText by rememberSaveable { mutableStateOf(initialData.phoneNumber) }

    var selectedHealthGoal by rememberSaveable { mutableStateOf(initialData.primaryHealthGoal) }
    var selectedConditionId by rememberSaveable { mutableStateOf(initialData.primaryConditionId) }

    val selectedCondition = remember(selectedConditionId) {
        DiseaseCatalog.getConditionById(selectedConditionId)
    }

    val selectedSymptoms = remember {
        mutableStateListOf<String>().apply {
            if (initialData.selectedSymptoms.isNotEmpty()) {
                addAll(initialData.selectedSymptoms)
            } else {
                addAll(selectedCondition.targetSymptoms.take(2))
            }
        }
    }
    var customSymptomNote by rememberSaveable { mutableStateOf("") }

    var baselinePain by rememberSaveable { mutableIntStateOf(initialData.baselinePainScore) }
    var baselineMobility by rememberSaveable { mutableIntStateOf(initialData.baselineMobilityScore) }
    var baselineStress by rememberSaveable { mutableIntStateOf(initialData.baselineStressScore) }

    var mobilityLevel by rememberSaveable { mutableStateOf(initialData.mobilityLevel) }
    var yogaExperience by rememberSaveable { mutableStateOf(initialData.yogaExperience) }
    var availableMinutes by rememberSaveable { mutableIntStateOf(initialData.availablePracticeMinutes) }
    var dietaryType by rememberSaveable { mutableStateOf(initialData.dietaryType) }
    var redFlagsDetected by rememberSaveable { mutableStateOf(initialData.redFlagsDetected) }

    val currentBmi = remember(heightCmValue, weightKgValue) {
        calculateBmi(heightCmValue, weightKgValue)
    }
    val bmiCategoryLabel = remember(currentBmi) {
        describeBmiCategory(currentBmi)
    }

    // Combine condition-specific symptoms with core routine symptoms without duplicates
    val allAvailableSymptoms = remember(selectedCondition.id) {
        (selectedCondition.targetSymptoms + CoreRoutineSymptoms).distinct()
    }

    // Calculate completion progress based on filled fields
    val readinessProgress = remember(
        fullNameText,
        ageValue,
        heightCmValue,
        weightKgValue,
        selectedHealthGoal,
        selectedSymptoms.size
    ) {
        var score = 0.20f
        if (fullNameText.isNotBlank()) score += 0.20f
        if (selectedHealthGoal.isNotBlank()) score += 0.30f
        if (selectedSymptoms.isNotEmpty()) score += 0.30f
        score.coerceIn(0f, 1f)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .testTag("onboarding_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp)
                .align(Alignment.TopCenter)
                .testTag("onboarding_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header Banner & Readiness Progress
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color.White.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Spa,
                                        contentDescription = null,
                                        tint = Color(0xFFFBF8F3),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "PERSONALIZED DAILY ROUTINE BUILDER",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFFBF8F3),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            TextButton(
                                onClick = onSkipOnboarding,
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color(0xFFFBF8F3)
                                ),
                                modifier = Modifier.testTag("onboarding_skip_button")
                            ) {
                                Text("Skip for Now")
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Welcome to YogaSwasthya",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFFFBF8F3),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tell us your age, primary health goal, and current symptoms so we can tailor a safe, conservative Morning, Afternoon, Evening & Night yoga therapy routine.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFE6EFEA)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        LinearProgressIndicator(
                            progress = { readinessProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50)),
                            color = Color(0xFFD4A359),
                            trackColor = Color.White.copy(alpha = 0.25f)
                        )
                    }
                }
            }

            // 2. STEP 1: User's Age, Age Group & Mobility Tolerance
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "1. Personal Details, Body Metrics & Medical History",
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

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = fullNameText,
                            onValueChange = { fullNameText = it.take(80) },
                            label = { Text("Full Name") },
                            placeholder = { Text("e.g., Aarav Sharma") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_name_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = emailIdText,
                                onValueChange = { emailIdText = it.take(120) },
                                label = { Text("Mail ID (Email)") },
                                placeholder = { Text("name@example.com") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("onboarding_email_input")
                            )

                            OutlinedTextField(
                                value = phoneNumberText,
                                onValueChange = { raw ->
                                    val filtered = raw.filter { it.isDigit() || it in "+- ()" }.take(25)
                                    phoneNumberText = filtered
                                },
                                label = { Text("Phone No.") },
                                placeholder = { Text("+91 98765 43210") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("onboarding_phone_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = heightInputText,
                                onValueChange = { input ->
                                    val digits = input.filter { it.isDigit() }.take(3)
                                    heightInputText = digits
                                    digits.toIntOrNull()?.let { parsed ->
                                        if (parsed in 50..260) {
                                            heightCmValue = parsed
                                        }
                                    }
                                },
                                label = { Text("Height (cm)") },
                                placeholder = { Text("165") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("onboarding_height_input")
                            )

                            OutlinedTextField(
                                value = weightInputText,
                                onValueChange = { input ->
                                    val digits = input.filter { it.isDigit() }.take(3)
                                    weightInputText = digits
                                    digits.toIntOrNull()?.let { parsed ->
                                        if (parsed in 15..300) {
                                            weightKgValue = parsed
                                        }
                                    }
                                },
                                label = { Text("Weight (kg)") },
                                placeholder = { Text("68") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("onboarding_weight_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_bmi_badge")
                        ) {
                            Text(
                                text = String.format(
                                    Locale.US,
                                    "Anthropometric Summary: %d cm • %d kg • BMI %.1f kg/m² (%s)",
                                    heightCmValue,
                                    weightKgValue,
                                    currentBmi,
                                    bmiCategoryLabel
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = knownDiseaseOrMedText,
                            onValueChange = { knownDiseaseOrMedText = it.take(300) },
                            label = { Text("Known Disease or Medication") },
                            placeholder = { Text("e.g., Hypertension (Amlodipine 5mg), Type 2 Diabetes, Vitamin D3") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_known_disease_input")
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Quick-add common conditions or medications:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                "Hypertension / BP Meds",
                                "Type 2 Diabetes",
                                "Hypothyroidism",
                                "Cervical / Lumbar Spondylitis",
                                "Osteoarthritis / Joint Meds",
                                "Vitamin D3 / B12",
                                "None Reported"
                            ).forEach { itemTag ->
                                val isPresent = knownDiseaseOrMedText.contains(itemTag, ignoreCase = true)
                                FilterChip(
                                    selected = isPresent,
                                    onClick = {
                                        knownDiseaseOrMedText = if (itemTag == "None Reported") {
                                            "None Reported"
                                        } else if (isPresent) {
                                            knownDiseaseOrMedText
                                                .split(",")
                                                .map { it.trim() }
                                                .filterNot { it.equals(itemTag, ignoreCase = true) }
                                                .joinToString(", ")
                                        } else {
                                            val currentClean = knownDiseaseOrMedText
                                                .removePrefix("None Reported")
                                                .trim()
                                                .trimEnd(',', '•')
                                                .trim()
                                            if (currentClean.isBlank()) itemTag else "$currentClean, $itemTag"
                                        }
                                    },
                                    label = { Text(itemTag, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = ageInputText,
                                onValueChange = { input ->
                                    val digits = input.filter { it.isDigit() }.take(2)
                                    ageInputText = digits
                                    val parsed = digits.toIntOrNull()
                                    if (parsed != null && parsed in 12..99) {
                                        ageValue = parsed.coerceIn(18, 95)
                                        selectedAgeGroup = deriveAgeGroupLabel(ageValue)
                                        if (ageValue >= 65) {
                                            mobilityLevel = "Chair / Wall Support Needed"
                                        }
                                    }
                                },
                                label = { Text("Age (Years)") },
                                singleLine = true,
                                modifier = Modifier
                                    .width(135.dp)
                                    .testTag("onboarding_age_input")
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Age: $ageValue years ($selectedAgeGroup)",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Slider(
                                    value = ageValue.toFloat(),
                                    onValueChange = { newVal ->
                                        val rounded = newVal.toInt().coerceIn(18, 95)
                                        ageValue = rounded
                                        ageInputText = rounded.toString()
                                        selectedAgeGroup = deriveAgeGroupLabel(rounded)
                                        if (rounded >= 65) {
                                            mobilityLevel = "Chair / Wall Support Needed"
                                        }
                                    },
                                    valueRange = 18f..95f,
                                    modifier = Modifier.testTag("onboarding_age_slider")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Age Group Bracket", style = MaterialTheme.typography.labelLarge)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("18–34 yrs", "35–50 yrs", "51–65 yrs", "65+ Senior").forEach { bracket ->
                                FilterChip(
                                    selected = selectedAgeGroup == bracket,
                                    onClick = {
                                        selectedAgeGroup = bracket
                                        val defaultAgeForBracket = when (bracket) {
                                            "18–34 yrs" -> 28
                                            "35–50 yrs" -> 42
                                            "51–65 yrs" -> 58
                                            else -> 68
                                        }
                                        ageValue = defaultAgeForBracket
                                        ageInputText = defaultAgeForBracket.toString()
                                        if (bracket == "65+ Senior") {
                                            mobilityLevel = "Chair / Wall Support Needed"
                                        }
                                    },
                                    label = { Text(bracket) },
                                    modifier = Modifier.testTag("age_group_chip_$bracket")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Current Mobility & Support Preference", style = MaterialTheme.typography.labelLarge)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                "Chair / Wall Support Needed",
                                "Moderate (Occasional Stiffness)",
                                "Active Preventive"
                            ).forEach { mob ->
                                FilterChip(
                                    selected = mobilityLevel == mob,
                                    onClick = { mobilityLevel = mob },
                                    label = { Text(mob) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Yoga Experience", style = MaterialTheme.typography.labelLarge)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Beginner", "Intermediate", "Experienced").forEach { exp ->
                                FilterChip(
                                    selected = yogaExperience == exp,
                                    onClick = { yogaExperience = exp },
                                    label = { Text(exp) }
                                )
                            }
                        }
                    }
                }
            }

            // 3. STEP 2: Primary Health Goal & Condition Selection
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Flag,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "2. Primary Health Goal & Target Condition",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Select your #1 wellness or recovery objective",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrimaryHealthGoals.forEach { goal ->
                                val isSelected = selectedHealthGoal == goal.title
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            selectedHealthGoal = goal.title
                                            selectedConditionId = goal.recommendedConditionId
                                            val newCond = DiseaseCatalog.getConditionById(goal.recommendedConditionId)
                                            selectedSymptoms.clear()
                                            selectedSymptoms.addAll(newCond.targetSymptoms.take(2))
                                        }
                                        .testTag("onboarding_goal_${goal.id}"),
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = goal.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = if (isSelected)
                                                    MaterialTheme.colorScheme.onPrimaryContainer
                                                else
                                                    MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = goal.subtitle,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Selected",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Specific Condition Focus (18 Clinical & Preventive Programs)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DiseaseCatalog.conditions.forEach { cond ->
                                val isChosen = cond.id == selectedConditionId
                                FilterChip(
                                    selected = isChosen,
                                    onClick = {
                                        selectedConditionId = cond.id
                                        selectedSymptoms.clear()
                                        selectedSymptoms.addAll(cond.targetSymptoms.take(2))
                                    },
                                    label = { Text(cond.shortName) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    modifier = Modifier.testTag("onboarding_condition_${cond.id}")
                                )
                            }
                        }
                    }
                }
            }

            // 4. STEP 3: Current Symptoms & Severity Assessment
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Healing,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "3. Current Symptoms & Baseline Severity",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Select all symptoms you currently experience (${selectedSymptoms.size} selected)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            allAvailableSymptoms.forEachIndexed { index, symptom ->
                                val isChecked = symptom in selectedSymptoms
                                FilterChip(
                                    selected = isChecked,
                                    onClick = {
                                        if (isChecked) {
                                            selectedSymptoms.remove(symptom)
                                        } else {
                                            selectedSymptoms.add(symptom)
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = symptom,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    leadingIcon = if (isChecked) {
                                        {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    } else null,
                                    modifier = Modifier.testTag("onboarding_symptom_chip_$index")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = customSymptomNote,
                            onValueChange = { customSymptomNote = it.take(120) },
                            label = { Text("Additional Symptom or Functional Difficulty (Optional)") },
                            placeholder = { Text("e.g., Stiffness after 30 mins of desk work, difficulty climbing stairs") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_custom_symptom_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Current Pain / Discomfort Level: $baselinePain / 10",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Slider(
                            value = baselinePain.toFloat(),
                            onValueChange = { baselinePain = it.toInt() },
                            valueRange = 0f..10f,
                            steps = 9,
                            modifier = Modifier.testTag("onboarding_pain_slider")
                        )

                        Text(
                            text = "Current Joint / Spine Mobility: $baselineMobility / 10 (Higher is freer)",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Slider(
                            value = baselineMobility.toFloat(),
                            onValueChange = { baselineMobility = it.toInt() },
                            valueRange = 1f..10f,
                            steps = 8,
                            modifier = Modifier.testTag("onboarding_mobility_slider")
                        )

                        Text(
                            text = "Current Stress / Mental Fatigue: $baselineStress / 10",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Slider(
                            value = baselineStress.toFloat(),
                            onValueChange = { baselineStress = it.toInt() },
                            valueRange = 0f..10f,
                            steps = 9,
                            modifier = Modifier.testTag("onboarding_stress_slider")
                        )
                    }
                }
            }

            // 5. STEP 4: Daily Routine Schedule & Pre-Session Safety Check
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "4. Daily Routine Builder & Safety Check",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Available Daily Practice Time", style = MaterialTheme.typography.labelLarge)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(10, 15, 20, 30, 45).forEach { mins ->
                                FilterChip(
                                    selected = availableMinutes == mins,
                                    onClick = { availableMinutes = mins },
                                    label = { Text("$mins min/day") }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Dietary Preference (For Indian Meal Guide)", style = MaterialTheme.typography.labelLarge)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Vegetarian", "Vegetarian + Egg", "Non-Vegetarian", "Vegan / Dairy-Free").forEach { diet ->
                                FilterChip(
                                    selected = dietaryType == diet,
                                    onClick = { dietaryType = diet },
                                    label = { Text(diet) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = redFlagsDetected,
                                onCheckedChange = {
                                    redFlagsDetected = it
                                    if (it) {
                                        mobilityLevel = "Chair / Wall Support Needed"
                                    }
                                },
                                modifier = Modifier.testTag("onboarding_red_flag_checkbox")
                            )
                            Text(
                                text = "I have recent surgery, severe osteoporosis, dizziness/balance risk, uncontrolled BP, or acute radiating nerve pain (enables Chair/Restorative safety mode).",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Live Preview of the Generated 4-Slot Daily Routine
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Your Tailored Daily Routine Preview ($availableMinutes min/day • $selectedAgeGroup)",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• Morning (10–15m): ${selectedCondition.asanas.firstOrNull()?.title ?: "Gentle Joint & Spine Mobility"} ($mobilityLevel)\n" +
                                        "• Afternoon (3–5m): Ergonomic Posture & Scapular Micro-Break\n" +
                                        "• Evening (5–10m): ${selectedCondition.pranayamas.firstOrNull()?.title ?: "Screened Slow Diaphragmatic Breathing"} (Zero Retention)\n" +
                                        "• Night (3–5m): ${selectedCondition.dhyanas.firstOrNull()?.title ?: "Body-Scan Relaxation"} & <2-Min Symptom Check-In",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val finalSymptoms = buildList {
                                    addAll(selectedSymptoms)
                                    if (customSymptomNote.isNotBlank()) {
                                        add(customSymptomNote.trim())
                                    }
                                }.ifEmpty {
                                    selectedCondition.targetSymptoms.take(2)
                                }

                                val updatedProfileData = initialData.copy(
                                    fullName = fullNameText.trim().ifBlank { initialData.fullName.ifBlank { "Practitioner" } },
                                    age = ageValue,
                                    ageGroup = selectedAgeGroup,
                                    heightCm = heightCmValue,
                                    weightKg = weightKgValue,
                                    knownDiseasesOrMedications = knownDiseaseOrMedText.trim().ifBlank { "None Reported" },
                                    emailId = emailIdText.trim(),
                                    phoneNumber = phoneNumberText.trim(),
                                    primaryHealthGoal = selectedHealthGoal,
                                    primaryConditionId = selectedConditionId,
                                    selectedSymptoms = finalSymptoms,
                                    baselinePainScore = baselinePain,
                                    baselineMobilityScore = baselineMobility,
                                    baselineStressScore = baselineStress,
                                    mobilityLevel = mobilityLevel,
                                    yogaExperience = yogaExperience,
                                    dietaryType = dietaryType,
                                    availablePracticeMinutes = availableMinutes,
                                    hasCompletedSafetyScreening = true,
                                    redFlagsDetected = redFlagsDetected,
                                    isOnboardingCompleted = true
                                )
                                onCompleteOnboarding(updatedProfileData)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("onboarding_complete_button"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SelfImprovement,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save Profile & Start My Daily Routine",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
