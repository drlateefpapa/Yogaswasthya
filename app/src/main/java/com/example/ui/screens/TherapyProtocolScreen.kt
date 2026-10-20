package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.catalog.DiseaseCatalog
import com.example.data.catalog.MeditationCatalog
import com.example.data.model.DiseaseCondition
import com.example.data.model.MeditationTrack
import com.example.data.model.PatientProfile
import com.example.data.model.PracticeCategory
import com.example.data.model.TherapeuticPractice
import com.example.ui.components.InteractiveBreathMandala
import com.example.ui.components.TherapeuticHeroBannerIllustration
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TherapyProtocolScreen(
    selectedCondition: DiseaseCondition,
    patientProfile: PatientProfile?,
    onSelectCondition: (String) -> Unit,
    onStartPractice: (TherapeuticPractice) -> Unit,
    onStartMeditationTrack: (MeditationTrack, Int) -> Unit = { _, _ -> },
    onSaveBaselineProfile: (
        conditionId: String,
        baselinePain: Int,
        baselineMobility: Int,
        baselineStress: Int,
        dietPref: String,
        dailyGoalMins: Int
    ) -> Unit,
    onNavigateToDiet: () -> Unit,
    showFullConditionDetailsByDefault: Boolean = true,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(PracticeCategory.ASANA) }
    var showBaselineSheet by rememberSaveable { mutableStateOf(false) }
    var conditionDetailsExpanded by rememberSaveable(showFullConditionDetailsByDefault) {
        mutableStateOf(showFullConditionDetailsByDefault)
    }

    val filteredConditions = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            DiseaseCatalog.conditions
        } else {
            val q = searchQuery.trim().lowercase()
            DiseaseCatalog.conditions.filter { cond ->
                cond.name.lowercase().contains(q) ||
                    cond.shortName.lowercase().contains(q) ||
                    cond.categoryTag.lowercase().contains(q) ||
                    cond.clinicalSubtitle.lowercase().contains(q) ||
                    cond.targetSymptoms.any { it.lowercase().contains(q) }
            }
        }
    }

    val currentPractices = when (selectedCategory) {
        PracticeCategory.ASANA -> selectedCondition.asanas
        PracticeCategory.PRANAYAMA -> selectedCondition.pranayamas
        PracticeCategory.DHYANA -> selectedCondition.dhyanas
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("therapy_protocol_list"),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Banner with Disease Condition Context
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                TherapeuticHeroBannerIllustration(
                    accentColor = Color(selectedCondition.accentHex),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(195.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = selectedCondition.categoryTag.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFBF8F3),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }

                        if (patientProfile?.primaryConditionId == selectedCondition.id) {
                            Surface(
                                color = Color(0xFFD4A359),
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkAdded,
                                        contentDescription = null,
                                        tint = Color(0xFF18241E),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Primary Focus",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF18241E),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = selectedCondition.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFFBF8F3),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = selectedCondition.clinicalSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE6EFEA)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { showBaselineSheet = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFBF8F3),
                                contentColor = Color(0xFF143325)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("configure_baseline_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Healing,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (patientProfile?.primaryConditionId == selectedCondition.id)
                                    "Update Baseline"
                                else
                                    "Set as Primary Focus",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateToDiet,
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFFBF8F3)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("quick_diet_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "View Diet Protocol",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

        // 2. Search & Condition Selector Bar (All 17+ Conditions)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("Search 18 conditions (e.g., OA, Cervical, Lumbar, Frozen Shoulder, Migraine, Diabetes)")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search conditions"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("condition_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredConditions.forEach { condition ->
                        val isSelected = condition.id == selectedCondition.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectCondition(condition.id) },
                            label = {
                                Text(
                                    text = condition.shortName,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("condition_chip_${condition.id}")
                        )
                    }
                }
            }
        }

        // 3. Comprehensive Disease-Oriented Clinical & Yoga Therapy Overview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Condition Overview & Safety Protocol",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Supportive care for symptom & mobility management (does not replace medical care)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { conditionDetailsExpanded = !conditionDetailsExpanded }) {
                            Icon(
                                imageVector = if (conditionDetailsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (conditionDetailsExpanded) "Collapse condition guide" else "Expand condition guide"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = selectedCondition.overview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Ayurvedic & Biomechanical Perspective",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = selectedCondition.rootCauseAyurvedic,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Common Symptoms & Functional Difficulties",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        selectedCondition.targetSymptoms.forEach { symptom ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "• $symptom",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = conditionDetailsExpanded) {
                        Column(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HorizontalDivider()

                            // Goals of the Yoga Program
                            Text(
                                text = "Goals of the Therapeutic Yoga Program",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            selectedCondition.programGoals.forEach { goal ->
                                Text(
                                    text = "✓ $goal",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Aggravating Movements to Avoid
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Block,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.tertiary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Unsuitable or Potentially Aggravating Movements",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    selectedCondition.aggravatingMovementsToAvoid.forEach { mov ->
                                        Text(
                                            text = "• $mov",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                }
                            }

                            // Modifications for Beginners, Seniors & Flare-Up Days
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AccessibilityNew,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Beginner, Senior & Flare-Up Day Modifications",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = selectedCondition.flareUpAndMobilityModifications,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            // Weekly Schedule & Measurable Progress Indicators
                            Text(
                                text = "Recommended Daily & Weekly Schedule",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = selectedCondition.weeklyScheduleSummary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Symptom Monitoring & Measurable Progress Indicators",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            selectedCondition.trackedFunctionalMetrics.forEach { metric ->
                                Text(
                                    text = "• $metric",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Criteria for Medical Referral
                            Surface(
                                color = Color(0xFFFCE6DC),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocalHospital,
                                            contentDescription = null,
                                            tint = Color(0xFFB85D3B),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Criteria for Immediate Medical Referral",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color(0xFF3E1809),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    selectedCondition.medicalReferralCriteria.forEach { crit ->
                                        Text(
                                            text = "• $crit",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF3E1809)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Three Pillars Segmented Selector: Asana, Pranayama, Dhyana
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Therapeutic Yoga Prescription",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PracticeCategory.entries.forEach { category ->
                        val count = when (category) {
                            PracticeCategory.ASANA -> selectedCondition.asanas.size
                            PracticeCategory.PRANAYAMA -> selectedCondition.pranayamas.size
                            PracticeCategory.DHYANA -> selectedCondition.dhyanas.size + MeditationCatalog.tracks.size
                        }
                        val isSelected = selectedCategory == category
                        val icon = when (category) {
                            PracticeCategory.ASANA -> Icons.Default.SelfImprovement
                            PracticeCategory.PRANAYAMA -> Icons.Default.Air
                            PracticeCategory.DHYANA -> Icons.Default.Psychology
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedCategory = category }
                                .testTag("pillar_tab_${category.name.lowercase()}"),
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = category.label,
                                    tint = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${category.label} ($count)",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4b. Pranayama Safety Screening Banner when PRANAYAMA tab is active (Section 6)
        if (selectedCategory == PracticeCategory.PRANAYAMA) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pranayama Clinical Screening & Safety Rules",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Screened Safe Techniques: Slow Diaphragmatic Breathing, Coherent Breathing, Box Breathing (without aggressive retention), Nadi Shodhana (without breath holding), and Bhramari.\n" +
                                "• Strictly Excluded from Auto-Prescription: Forceful Kapalabhati, rapid Bhastrika, and prolonged Kumbhaka (breath retention) are never prescribed for uncontrolled hypertension, cardiac disease, acute migraine, panic disorder, dizziness/vertigo, or pregnancy.\n" +
                                "• Stop Signal: If you feel lightheaded, dizzy, chest tightness, or anxious, stop immediately and resume normal effortless nasal breathing.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        // 5. Practice Cards for Selected Pillar
        items(currentPractices, key = { it.id }) { practice ->
            TherapeuticPracticeCard(
                practice = practice,
                onStartGuidedSession = { onStartPractice(practice) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 6. Dedicated Meditation & Relaxation Library when DHYANA tab is active (Section 7)
        if (selectedCategory == PracticeCategory.DHYANA) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Guided Meditation & Relaxation Library (3, 5, 10 & 15 Min)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Offline-friendly written guides + ambient audio pacing for stress, sleep & pain coping",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(MeditationCatalog.tracks, key = { it.id }) { track ->
                MeditationTrackCard(
                    track = track,
                    onStartTrack = { duration -> onStartMeditationTrack(track, duration) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }

    if (showBaselineSheet) {
        BaselineConfigurationDialog(
            condition = selectedCondition,
            existingProfile = patientProfile,
            onDismiss = { showBaselineSheet = false },
            onConfirm = { pain, mobility, stress, diet, goalMins ->
                onSaveBaselineProfile(
                    selectedCondition.id,
                    pain,
                    mobility,
                    stress,
                    diet,
                    goalMins
                )
                showBaselineSheet = false
            }
        )
    }
}

@Composable
private fun MeditationTrackCard(
    track: MeditationTrack,
    onStartTrack: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDuration by rememberSaveable(track.id) { mutableIntStateOf(5) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "AMBIENT: ${track.ambientSoundLabel.uppercase()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = track.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = track.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = track.targetPurpose,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Written Guide (Offline Ready):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            track.writtenGuideSteps.forEachIndexed { i, step ->
                Text(
                    text = "${i + 1}. $step",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Duration:", style = MaterialTheme.typography.labelMedium)
                track.availableDurationsMinutes.forEach { mins ->
                    FilterChip(
                        selected = selectedDuration == mins,
                        onClick = { selectedDuration = mins },
                        label = { Text("${mins}m") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onStartTrack(selectedDuration) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Start $selectedDuration-Min Guided Relaxation")
            }
        }
    }
}

@Composable
private fun TherapeuticPracticeCard(
    practice: TherapeuticPractice,
    onStartGuidedSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable(practice.id) { mutableStateOf(true) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("practice_card_${practice.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = practice.category.label.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${practice.durationMinutes} min • ${practice.difficulty}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = practice.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = practice.sanskritName,
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse details" else "Expand details"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Muscles / Movement Areas: ${practice.anatomicalFocus}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Recommended Duration / Repetitions: ${practice.holdAndReps}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    // Starting Position
                    Text(
                        text = "Starting Position: ${practice.startingPosition}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Breathing Cue Banner
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Air,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Breathing Instructions (${practice.inhaleSeconds}s Inhale : ${practice.exhaleSeconds}s Exhale — No Forced Retention)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = practice.breathingCue,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Step-by-Step Instructions",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    practice.stepByStepInstructions.forEachIndexed { idx, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${idx + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Chair Yoga, Wall Support & Gentle Modifications
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Chair Yoga, Wall Support & Limited-Mobility Option",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = practice.chairAndWallModifications,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Expected Functional Benefits",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    practice.therapeuticBenefits.forEach { benefit ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = benefit,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Common Mistakes to Avoid",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    practice.commonMistakes.forEach { mistake ->
                        Text(
                            text = "• $mistake",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Safely Exiting the Pose: ${practice.safeExitInstructions}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Contraindication & Red-Flag Stop Signals Box
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WarningAmber,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Contraindications & Stop Signals",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            practice.contraindications.forEach { warn ->
                                Text(
                                    text = "• $warn",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "STOP IMMEDIATELY IF: ${practice.stopSignals.joinToString("; ")}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF8C2D19),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onStartGuidedSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("start_practice_${practice.id}"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Start Guided ${practice.category.label} Pacer (${practice.durationMinutes} min)",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun GuidedPracticeSessionModal(
    practice: TherapeuticPractice,
    conditionName: String,
    onDismiss: () -> Unit,
    onCompleteSession: (durationMinutes: Int, reliefRating: Int) -> Unit
) {
    val totalSecondsInitial = (practice.durationMinutes * 60).coerceAtLeast(60)
    var remainingSeconds by remember(practice.id) { mutableIntStateOf(totalSecondsInitial) }
    var isRunning by remember(practice.id) { mutableStateOf(true) }
    var activeStepIndex by remember(practice.id) { mutableIntStateOf(0) }
    var reliefRating by remember(practice.id) { mutableIntStateOf(5) }
    var breathTick by remember { mutableIntStateOf(0) }

    LaunchedEffect(isRunning, remainingSeconds) {
        if (isRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds -= 1
            breathTick = (breathTick + 1) % 10
        }
    }

    // Screened zero-retention 10-second breath cycle: 4s Inhale -> 6s Extended Exhale
    val (breathPhase, phaseCountdown) = when (breathTick) {
        in 0..3 -> "Inhale Gently (Nose)" to (4 - breathTick)
        else -> "Slow Exhale (No Hold)" to (10 - breathTick)
    }

    val mins = remainingSeconds / 60
    val secs = remainingSeconds % 60
    val formattedTime = String.format("%02d:%02d", mins, secs)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val elapsedMinutes = ((totalSecondsInitial - remainingSeconds) / 60)
                        .coerceAtLeast(practice.durationMinutes)
                    onCompleteSession(elapsedMinutes, reliefRating)
                },
                modifier = Modifier.testTag("complete_session_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Complete & Log Relief")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = {
            Column {
                Text(
                    text = practice.title,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "$conditionName • ${practice.sanskritName}",
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InteractiveBreathMandala(
                    phaseLabel = breathPhase,
                    secondsRemainingInCycle = phaseCountdown,
                    modifier = Modifier.size(165.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { isRunning = !isRunning },
                        modifier = Modifier.testTag("toggle_timer_button")
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isRunning) "Pause timer" else "Resume timer",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isRunning) "Pause" else "Resume")
                    }

                    IconButton(
                        onClick = {
                            remainingSeconds = totalSecondsInitial
                            breathTick = 0
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = "Reset timer"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Step ${activeStepIndex + 1} of ${practice.stepByStepInstructions.size} (Captions On)",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Row {
                                TextButton(
                                    onClick = {
                                        if (activeStepIndex > 0) activeStepIndex -= 1
                                    },
                                    enabled = activeStepIndex > 0
                                ) {
                                    Text("Prev")
                                }
                                TextButton(
                                    onClick = {
                                        if (activeStepIndex < practice.stepByStepInstructions.lastIndex) {
                                            activeStepIndex += 1
                                        }
                                    },
                                    enabled = activeStepIndex < practice.stepByStepInstructions.lastIndex
                                ) {
                                    Text("Next")
                                }
                            }
                        }
                        Text(
                            text = practice.stepByStepInstructions.getOrElse(activeStepIndex) { "" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Stop Signal: Pause immediately if you feel sharp pain, dizziness, numbness, or breathlessness.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFB85D3B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Post-Practice Comfort & Symptom Relief Rating",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (star in 1..5) {
                        IconButton(
                            onClick = { reliefRating = star },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = if (star <= reliefRating) Icons.Default.Star else Icons.Outlined.StarOutline,
                                contentDescription = "Rate $star stars",
                                tint = Color(0xFFC98A2E),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun BaselineConfigurationDialog(
    condition: DiseaseCondition,
    existingProfile: PatientProfile?,
    onDismiss: () -> Unit,
    onConfirm: (pain: Int, mobility: Int, stress: Int, diet: String, goalMins: Int) -> Unit
) {
    var baselinePain by remember { mutableIntStateOf(existingProfile?.baselinePainScore ?: 7) }
    var baselineMobility by remember { mutableIntStateOf(existingProfile?.baselineMobilityScore ?: 4) }
    var baselineStress by remember { mutableIntStateOf(existingProfile?.baselineStressScore ?: 6) }
    var goalMinutes by remember { mutableIntStateOf(existingProfile?.dailyGoalMinutes ?: 30) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Set ${condition.shortName} Recovery Baseline",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Your baseline scores help YogaSwasthya track your self-reported symptom and mobility trends over time.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Baseline Pain Severity: $baselinePain / 10",
                    style = MaterialTheme.typography.labelLarge
                )
                Slider(
                    value = baselinePain.toFloat(),
                    onValueChange = { baselinePain = it.toInt() },
                    valueRange = 0f..10f,
                    steps = 9
                )

                Text(
                    text = "Baseline Joint/Spine Mobility: $baselineMobility / 10",
                    style = MaterialTheme.typography.labelLarge
                )
                Slider(
                    value = baselineMobility.toFloat(),
                    onValueChange = { baselineMobility = it.toInt() },
                    valueRange = 1f..10f,
                    steps = 8
                )

                Text(
                    text = "Baseline Stress / Tension: $baselineStress / 10",
                    style = MaterialTheme.typography.labelLarge
                )
                Slider(
                    value = baselineStress.toFloat(),
                    onValueChange = { baselineStress = it.toInt() },
                    valueRange = 0f..10f,
                    steps = 9
                )

                Text(
                    text = "Daily Practice Target: $goalMinutes mins/day",
                    style = MaterialTheme.typography.labelLarge
                )
                Slider(
                    value = goalMinutes.toFloat(),
                    onValueChange = { goalMinutes = (it.toInt() / 5) * 5 },
                    valueRange = 10f..90f,
                    steps = 15
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        baselinePain,
                        baselineMobility,
                        baselineStress,
                        condition.dietaryProtocol.doshaBalance.take(50),
                        goalMinutes.coerceIn(5, 180)
                    )
                },
                modifier = Modifier.testTag("save_baseline_button")
            ) {
                Text("Save Baseline")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
