package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.DiseaseCondition

/**
 * Standalone Jetpack Compose screen for Daily Symptom & Habit Tracking, featuring:
 * - Sliders for Pain (0–10), Joint/Spinal Stiffness (0–10), and Mood & Emotional Wellbeing (1–10)
 * - Checkboxes for Yoga (Asana), Pranayama (Breathing), and Meditation (Dhyan) completion
 * - Expandable clinical metrics for Mobility, Sleep Quality, Diet Adherence, Hydration & Notes
 */
@Composable
fun DailySymptomTrackerScreen(
    selectedCondition: DiseaseCondition,
    initialPainLevel: Int = 4,
    initialStiffnessLevel: Int = 4,
    initialMoodLevel: Int = 7,
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
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("daily_symptom_tracker_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DailySymptomAndHabitTrackerCard(
                selectedCondition = selectedCondition,
                initialPainLevel = initialPainLevel,
                initialStiffnessLevel = initialStiffnessLevel,
                initialMoodLevel = initialMoodLevel,
                onSubmitDailyLog = onSubmitDailyLog
            )
        }
    }
}

/**
 * Reusable interactive card component for Daily Symptom (Pain, Stiffness, Mood sliders)
 * and Daily Habit (Yoga Asana, Pranayama, Meditation checkboxes) tracking.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailySymptomAndHabitTrackerCard(
    selectedCondition: DiseaseCondition,
    initialPainLevel: Int = 4,
    initialStiffnessLevel: Int = 4,
    initialMoodLevel: Int = 7,
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
    modifier: Modifier = Modifier
) {
    // Core Symptom Sliders: Pain, Stiffness, Mood
    var painLevel by rememberSaveable(selectedCondition.id) {
        mutableIntStateOf(initialPainLevel.coerceIn(0, 10))
    }
    var stiffnessLevel by rememberSaveable(selectedCondition.id) {
        mutableIntStateOf(initialStiffnessLevel.coerceIn(0, 10))
    }
    var moodLevel by rememberSaveable(selectedCondition.id) {
        mutableIntStateOf(initialMoodLevel.coerceIn(1, 10))
    }

    // Core Habit Checkboxes: Yoga (Asana), Pranayama, Meditation (Dhyan)
    var yogaCompleted by rememberSaveable(selectedCondition.id) { mutableStateOf(true) }
    var pranayamaCompleted by rememberSaveable(selectedCondition.id) { mutableStateOf(true) }
    var meditationCompleted by rememberSaveable(selectedCondition.id) { mutableStateOf(true) }
    var sattvicDietCompleted by rememberSaveable(selectedCondition.id) { mutableStateOf(true) }
    var postureHydrationCompleted by rememberSaveable(selectedCondition.id) { mutableStateOf(true) }

    // Minutes & Additional Recovery Metrics
    var asanaMinutes by rememberSaveable(selectedCondition.id) { mutableIntStateOf(15) }
    var pranayamaMinutes by rememberSaveable(selectedCondition.id) { mutableIntStateOf(10) }
    var dhyanaMinutes by rememberSaveable(selectedCondition.id) { mutableIntStateOf(10) }
    var mobilityScore by rememberSaveable(selectedCondition.id) { mutableIntStateOf(7) }
    var sleepQuality by rememberSaveable(selectedCondition.id) { mutableIntStateOf(8) }
    var waterGlasses by rememberSaveable(selectedCondition.id) { mutableIntStateOf(8) }
    var notes by rememberSaveable(selectedCondition.id) { mutableStateOf("") }
    var showAdvancedMetrics by rememberSaveable { mutableStateOf(false) }

    val allConditionPractices = remember(selectedCondition.id) {
        selectedCondition.asanas + selectedCondition.pranayamas + selectedCondition.dhyanas
    }
    val selectedPractices = remember(selectedCondition.id) {
        mutableStateListOf<String>().apply {
            selectedCondition.asanas.firstOrNull()?.let { add(it.title) }
            selectedCondition.pranayamas.firstOrNull()?.let { add(it.title) }
            selectedCondition.dhyanas.firstOrNull()?.let { add(it.title) }
        }
    }

    val completedHabitCount = listOf(
        yogaCompleted,
        pranayamaCompleted,
        meditationCompleted,
        sattvicDietCompleted,
        postureHydrationCompleted
    ).count { it }
    val habitProgress = completedHabitCount / 5f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_symptom_habit_tracker_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Healing,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Daily Symptom & Habit Tracker",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Track Pain, Stiffness, Mood & Daily Yoga Habits (${selectedCondition.shortName})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "$completedHabitCount/5 Habits",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // PART 1: DAILY SYMPTOM SLIDERS (Pain, Stiffness, Mood)
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "1. Daily Symptom & Wellbeing Sliders",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Pain Slider (0–10)
                    SymptomSliderRow(
                        title = "Pain Intensity",
                        valueLabel = "$painLevel / 10 • ${describePainSeverity(painLevel)}",
                        value = painLevel.toFloat(),
                        onValueChange = { painLevel = it.toInt() },
                        valueRange = 0f..10f,
                        steps = 9,
                        accentColor = Color(0xFFB85D3B),
                        testTag = "pain_slider"
                    )

                    // Stiffness Slider (0–10)
                    SymptomSliderRow(
                        title = "Joint & Spine Stiffness",
                        valueLabel = "$stiffnessLevel / 10 • ${describeStiffnessSeverity(stiffnessLevel)}",
                        value = stiffnessLevel.toFloat(),
                        onValueChange = { stiffnessLevel = it.toInt() },
                        valueRange = 0f..10f,
                        steps = 9,
                        accentColor = Color(0xFFC98A2E),
                        testTag = "stiffness_slider"
                    )

                    // Mood Slider (1–10)
                    SymptomSliderRow(
                        title = "Mood & Mental Calmness",
                        valueLabel = "$moodLevel / 10 • ${describeMoodState(moodLevel)}",
                        value = moodLevel.toFloat(),
                        onValueChange = { moodLevel = it.toInt() },
                        valueRange = 1f..10f,
                        steps = 8,
                        accentColor = Color(0xFF1F4E38),
                        testTag = "mood_slider"
                    )
                }
            }

            // PART 2: DAILY YOGA, PRANAYAMA & MEDITATION HABIT CHECKBOXES
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. Daily Practice & Recovery Habits",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${(habitProgress * 100).toInt()}% Complete",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    LinearProgressIndicator(
                        progress = { habitProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                    )

                    // Checkbox 1: Yoga Asana Practice Completion
                    HabitCheckboxRow(
                        checked = yogaCompleted,
                        onCheckedChange = { checked ->
                            yogaCompleted = checked
                            if (checked && asanaMinutes == 0) asanaMinutes = 15
                        },
                        title = "Yoga Asana Practice Completed",
                        subtitle = if (yogaCompleted) {
                            "${asanaMinutes} mins gentle therapeutic postures (${selectedCondition.asanas.firstOrNull()?.title ?: "Condition Asanas"})"
                        } else {
                            "Tap to mark today's therapeutic Asana sequence complete"
                        },
                        icon = Icons.Default.SelfImprovement,
                        minutes = if (yogaCompleted) asanaMinutes else 0,
                        onAdjustMinutes = { delta ->
                            asanaMinutes = (asanaMinutes + delta).coerceIn(5, 120)
                        },
                        checkboxTag = "habit_checkbox_yoga"
                    )

                    // Checkbox 2: Pranayama Breathing Completion
                    HabitCheckboxRow(
                        checked = pranayamaCompleted,
                        onCheckedChange = { checked ->
                            pranayamaCompleted = checked
                            if (checked && pranayamaMinutes == 0) pranayamaMinutes = 10
                        },
                        title = "Pranayama Breathing Completed",
                        subtitle = if (pranayamaCompleted) {
                            "${pranayamaMinutes} mins screened breath regulation (${selectedCondition.pranayamas.firstOrNull()?.title ?: "Nadi Shodhana / Diaphragmatic"})"
                        } else {
                            "Tap to mark today's Pranayama breathwork complete"
                        },
                        icon = Icons.Default.Air,
                        minutes = if (pranayamaCompleted) pranayamaMinutes else 0,
                        onAdjustMinutes = { delta ->
                            pranayamaMinutes = (pranayamaMinutes + delta).coerceIn(5, 60)
                        },
                        checkboxTag = "habit_checkbox_pranayama"
                    )

                    // Checkbox 3: Meditation (Dhyan) Completion
                    HabitCheckboxRow(
                        checked = meditationCompleted,
                        onCheckedChange = { checked ->
                            meditationCompleted = checked
                            if (checked && dhyanaMinutes == 0) dhyanaMinutes = 10
                        },
                        title = "Meditation (Dhyan) & Relaxation Completed",
                        subtitle = if (meditationCompleted) {
                            "${dhyanaMinutes} mins mindfulness / Yoga Nidra body scan (${selectedCondition.dhyanas.firstOrNull()?.title ?: "Guided Relaxation"})"
                        } else {
                            "Tap to mark today's Dhyan meditation complete"
                        },
                        icon = Icons.Default.Psychology,
                        minutes = if (meditationCompleted) dhyanaMinutes else 0,
                        onAdjustMinutes = { delta ->
                            dhyanaMinutes = (dhyanaMinutes + delta).coerceIn(5, 60)
                        },
                        checkboxTag = "habit_checkbox_meditation"
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))

                    // Checkbox 4: Supportive Dietary & Anti-Inflammatory Meal Timing
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { sattvicDietCompleted = !sattvicDietCompleted }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = sattvicDietCompleted,
                            onCheckedChange = { sattvicDietCompleted = it },
                            modifier = Modifier.testTag("habit_checkbox_diet")
                        )
                        Icon(
                            imageVector = Icons.Default.RestaurantMenu,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Followed Condition-Specific Sattvic Diet",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Warm, freshly prepared anti-inflammatory meals & timely dinner",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Checkbox 5: Warm Water Hydration & Posture Micro-Breaks
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { postureHydrationCompleted = !postureHydrationCompleted }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = postureHydrationCompleted,
                            onCheckedChange = { postureHydrationCompleted = it },
                            modifier = Modifier.testTag("habit_checkbox_hydration")
                        )
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Warm Hydration & Ergonomic Posture Breaks",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$waterGlasses glasses of water + hourly spine/joint micro-breaks",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Expandable Clinical Details (Mobility, Sleep, Specific Practices & Notes)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAdvancedMetrics = !showAdvancedMetrics }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showAdvancedMetrics)
                        "Hide Mobility, Sleep & Practice Notes"
                    else
                        "Add Mobility, Sleep Quality & Practice Notes (Optional)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (showAdvancedMetrics) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle additional metrics",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(visible = showAdvancedMetrics) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SymptomSliderRow(
                        title = "Functional Mobility & Task Comfort",
                        valueLabel = "$mobilityScore / 10",
                        value = mobilityScore.toFloat(),
                        onValueChange = { mobilityScore = it.toInt() },
                        valueRange = 1f..10f,
                        steps = 8,
                        accentColor = Color(0xFF1F4E38),
                        testTag = "mobility_slider"
                    )

                    SymptomSliderRow(
                        title = "Sleep Quality & Restorativeness",
                        valueLabel = "$sleepQuality / 10 (${sleepQuality / 2}/5 ★)",
                        value = sleepQuality.toFloat(),
                        onValueChange = { sleepQuality = it.toInt() },
                        valueRange = 2f..10f,
                        steps = 7,
                        accentColor = Color(0xFF347456),
                        testTag = "sleep_slider"
                    )

                    Text(
                        text = "Specific Practices Completed:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
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

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Symptom or Recovery Observations (Optional)") },
                        placeholder = { Text("e.g., Less morning neck stiffness after chair twists; mood calm") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("daily_tracker_notes_input")
                    )
                }
            }

            // Submit Button
            Button(
                onClick = {
                    val effectiveAsanaMins = if (yogaCompleted) asanaMinutes else 0
                    val effectivePranayamaMins = if (pranayamaCompleted) pranayamaMinutes else 0
                    val effectiveDhyanaMins = if (meditationCompleted) dhyanaMinutes else 0
                    // Map Mood (1..10, higher = calmer/better) to stressLevel (0..10, lower = calmer)
                    val derivedStressLevel = (10 - moodLevel).coerceIn(0, 10)
                    val derivedDietScore = when {
                        sattvicDietCompleted && postureHydrationCompleted -> 95
                        sattvicDietCompleted -> 85
                        postureHydrationCompleted -> 75
                        else -> 60
                    }
                    val moodSummaryNote = "Mood: $moodLevel/10 (${describeMoodState(moodLevel)})"
                    val combinedNotes = if (notes.isBlank()) {
                        moodSummaryNote
                    } else {
                        "$moodSummaryNote • ${notes.trim()}"
                    }

                    onSubmitDailyLog(
                        selectedCondition.id,
                        painLevel,
                        stiffnessLevel,
                        derivedStressLevel,
                        mobilityScore,
                        sleepQuality,
                        effectiveAsanaMins,
                        effectivePranayamaMins,
                        effectiveDhyanaMins,
                        selectedPractices.toList(),
                        derivedDietScore,
                        if (postureHydrationCompleted) waterGlasses else (waterGlasses / 2).coerceAtLeast(4),
                        combinedNotes
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_daily_tracking_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Today's Symptom & Habit Log",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SymptomSliderRow(
    title: String,
    valueLabel: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    accentColor: Color,
    testTag: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                color = accentColor.copy(alpha = 0.14f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = valueLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}

@Composable
private fun HabitCheckboxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    subtitle: String,
    icon: ImageVector,
    minutes: Int,
    onAdjustMinutes: (Int) -> Unit,
    checkboxTag: String
) {
    Surface(
        color = if (checked)
            MaterialTheme.colorScheme.surface
        else
            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCheckedChange(!checked) }
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.testTag(checkboxTag)
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (checked) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = { onAdjustMinutes(-5) },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("-", fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "${minutes}m",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(
                        onClick = { onAdjustMinutes(5) },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("+", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

fun describePainSeverity(pain: Int): String = when {
    pain == 0 -> "Pain-Free"
    pain <= 3 -> "Mild"
    pain <= 6 -> "Moderate"
    else -> "Severe (Use Restorative Mode)"
}

fun describeStiffnessSeverity(stiffness: Int): String = when {
    stiffness == 0 -> "Supple / No Stiffness"
    stiffness <= 3 -> "Mild Morning Stiffness"
    stiffness <= 6 -> "Moderate Restriction"
    else -> "High Stiffness (Gentle ROM Only)"
}

fun describeMoodState(mood: Int): String = when {
    mood <= 3 -> "Stressed / Fatigued"
    mood <= 6 -> "Steady / Moderate"
    mood <= 8 -> "Calm & Balanced"
    else -> "Peaceful & Energized"
}
