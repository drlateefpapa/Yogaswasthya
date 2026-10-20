package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WarningAmber
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.DiseaseCondition
import com.example.data.model.PatientProfile
import com.example.data.model.PracticeCategory
import com.example.data.model.TherapeuticPractice
import com.example.ui.components.TherapeuticHeroBannerIllustration

/**
 * Convenience overload that resolves any modular [DiseaseCondition] by its catalog ID
 * and renders [ConditionDetailScreen].
 */
@Composable
fun ConditionDetailScreen(
    conditionId: String,
    patientProfile: PatientProfile? = null,
    availableConditions: List<DiseaseCondition> = DiseaseCatalog.conditions,
    onSelectCondition: (String) -> Unit = {},
    onStartPractice: (TherapeuticPractice) -> Unit = {},
    onSetPrimaryCondition: (DiseaseCondition) -> Unit = {},
    onNavigateToPracticeLibrary: () -> Unit = {},
    onNavigateToDietProtocol: () -> Unit = {},
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val condition = remember(conditionId, availableConditions) {
        availableConditions.find { it.id == conditionId }
            ?: DiseaseCatalog.getConditionById(conditionId)
    }
    ConditionDetailScreen(
        condition = condition,
        patientProfile = patientProfile,
        availableConditions = availableConditions,
        onSelectCondition = onSelectCondition,
        onStartPractice = onStartPractice,
        onSetPrimaryCondition = onSetPrimaryCondition,
        onNavigateToPracticeLibrary = onNavigateToPracticeLibrary,
        onNavigateToDietProtocol = onNavigateToDietProtocol,
        onBack = onBack,
        modifier = modifier
    )
}

/**
 * Reusable, modular Jetpack Compose screen that dynamically displays structured clinical
 * and yoga therapy information for any [DiseaseCondition] in the catalog:
 * - Condition Overview, Ayurvedic/Biomechanical context & Common Symptoms
 * - Program Goals, Weekly Schedule, Mobility/Flare-Up Modifications & Measurable Indicators
 * - Contraindications, Aggravating Movements to Avoid & Medical Referral Red Flags
 * - Suitable Asanas, Screened Pranayama, Guided Dhyan & Dietary Highlights
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConditionDetailScreen(
    condition: DiseaseCondition,
    patientProfile: PatientProfile? = null,
    availableConditions: List<DiseaseCondition> = DiseaseCatalog.conditions,
    onSelectCondition: (String) -> Unit = {},
    onStartPractice: (TherapeuticPractice) -> Unit = {},
    onSetPrimaryCondition: (DiseaseCondition) -> Unit = {},
    onNavigateToPracticeLibrary: () -> Unit = {},
    onNavigateToDietProtocol: () -> Unit = {},
    onBack: (() -> Unit)? = null,
    showConditionSelectorBar: Boolean = true,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategoryFilter by rememberSaveable { mutableStateOf("All") }

    val categoryOptions = remember(availableConditions) {
        listOf("All") + availableConditions.map { it.categoryTag }.distinct()
    }

    val filteredConditions = remember(searchQuery, selectedCategoryFilter, availableConditions) {
        availableConditions.filter { item ->
            val matchesCategory = selectedCategoryFilter == "All" ||
                item.categoryTag.equals(selectedCategoryFilter, ignoreCase = true)
            val q = searchQuery.trim().lowercase()
            val matchesSearch = q.isBlank() ||
                item.name.lowercase().contains(q) ||
                item.shortName.lowercase().contains(q) ||
                item.clinicalSubtitle.lowercase().contains(q) ||
                item.targetSymptoms.any { it.lowercase().contains(q) }
            matchesCategory && matchesSearch
        }
    }

    val isPrimaryCondition = patientProfile?.primaryConditionId == condition.id

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("condition_detail_screen"),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Optional Modular Condition Switcher & Search Bar
        if (showConditionSelectorBar && availableConditions.size > 1) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text("Search ${availableConditions.size} conditions (OA, Spondylosis, Migraine, Anxiety...)")
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
                            .testTag("condition_detail_search_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category filter pills (Musculoskeletal, Mental Wellness, Neurological, Metabolic)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categoryOptions.forEach { category ->
                            val selected = selectedCategoryFilter == category
                            FilterChip(
                                selected = selected,
                                onClick = { selectedCategoryFilter = category },
                                label = {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                modifier = Modifier.testTag("condition_category_filter_${category.lowercase().replace(" ", "_")}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Horizontal condition selector chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filteredConditions.forEach { item ->
                            val isSelected = item.id == condition.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectCondition(item.id) },
                                label = {
                                    Text(
                                        text = item.shortName,
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
                                modifier = Modifier.testTag("condition_detail_chip_${item.id}")
                            )
                        }
                    }
                }
            }
        }

        // 2. Dynamic Condition Hero Banner
        item {
            ConditionHeroHeaderCard(
                condition = condition,
                isPrimaryCondition = isPrimaryCondition,
                onBack = onBack,
                onSetPrimaryCondition = { onSetPrimaryCondition(condition) },
                onNavigateToPracticeLibrary = onNavigateToPracticeLibrary,
                onNavigateToDietProtocol = onNavigateToDietProtocol
            )
        }

        // 3. Reusable Condition Overview, Symptoms, Goals & Contraindications Component
        item {
            ConditionDetailContentSections(
                condition = condition,
                onStartPractice = onStartPractice,
                onNavigateToPracticeLibrary = onNavigateToPracticeLibrary,
                onNavigateToDietProtocol = onNavigateToDietProtocol
            )
        }
    }
}

@Composable
fun ConditionHeroHeaderCard(
    condition: DiseaseCondition,
    isPrimaryCondition: Boolean,
    onBack: (() -> Unit)? = null,
    onSetPrimaryCondition: () -> Unit = {},
    onNavigateToPracticeLibrary: () -> Unit = {},
    onNavigateToDietProtocol: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .testTag("condition_detail_hero_banner")
    ) {
        TherapeuticHeroBannerIllustration(
            accentColor = Color(condition.accentHex),
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onBack != null) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { onBack() }
                                .testTag("condition_detail_back_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color(0xFFFBF8F3),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = condition.categoryTag.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFBF8F3),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }

                if (isPrimaryCondition) {
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
                                text = "Active Primary Focus",
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
                text = condition.name,
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFFBF8F3),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("condition_detail_title")
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = condition.clinicalSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE6EFEA)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onSetPrimaryCondition,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFBF8F3),
                        contentColor = Color(0xFF143325)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("condition_detail_set_primary_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Healing,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPrimaryCondition) "Primary Plan Active" else "Set as Primary Focus",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToPracticeLibrary,
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFBF8F3)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("condition_detail_open_practices_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SelfImprovement,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Practice Module",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

/**
 * Modular, reusable content sections displaying Overview, Program Goals, Contraindications,
 * Safety Red Flags, and Prescribed Practices for a [DiseaseCondition].
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConditionDetailContentSections(
    condition: DiseaseCondition,
    onStartPractice: (TherapeuticPractice) -> Unit = {},
    onNavigateToPracticeLibrary: () -> Unit = {},
    onNavigateToDietProtocol: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Aggregate unique practice-specific contraindications alongside condition-level precautions
    val aggregatedPracticeContraindications = remember(condition.id) {
        (condition.asanas + condition.pranayamas + condition.dhyanas)
            .flatMap { it.contraindications }
            .distinct()
            .take(6)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // SECTION 1: CONDITION OVERVIEW & TARGET SYMPTOMS
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("condition_detail_overview_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Healing,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Condition Overview",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Supportive yoga therapy care (does not claim to cure disease or replace medical care)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = condition.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("condition_detail_overview_text")
                )

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
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = condition.rootCauseAyurvedic,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Text(
                    text = "Common Symptoms & Functional Difficulties",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    condition.targetSymptoms.forEach { symptom ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "• $symptom",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // SECTION 2: PROGRAM GOALS, MODIFICATIONS & MEASURABLE INDICATORS
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("condition_detail_goals_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Goals of the Yoga Therapy Program",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Functional mobility, symptom modulation & nervous system balance",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.testTag("condition_detail_goals_list")
                ) {
                    condition.programGoals.forEach { goal ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = goal,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                HorizontalDivider()

                // Daily & Weekly Practice Schedule
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Daily & Weekly Practice Schedule",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = condition.weeklyScheduleSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Beginner, Senior & Restricted Mobility Modifications
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
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Modifications for Beginners, Seniors & Limited Mobility",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = condition.flareUpAndMobilityModifications,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Measurable Progress Indicators
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Symptom Monitoring & Measurable Progress Indicators",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                condition.trackedFunctionalMetrics.forEach { metric ->
                    Text(
                        text = "• $metric",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // SECTION 3: CONTRAINDICATIONS, AGGRAVATING MOVEMENTS & MEDICAL REFERRAL
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("condition_detail_contraindications_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Contraindications & Safety Precautions",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Movements to avoid, clinical precautions & referral criteria",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Core Condition Precautions Summary
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("condition_detail_precautions_banner")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Clinical Contraindication Summary",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = condition.precautionsSummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                // Unsuitable or Potentially Aggravating Movements
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f),
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
                        Spacer(modifier = Modifier.height(6.dp))
                        condition.aggravatingMovementsToAvoid.forEach { movement ->
                            Text(
                                text = "✕ $movement",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }

                // Practice-Specific Contraindications
                if (aggregatedPracticeContraindications.isNotEmpty()) {
                    Text(
                        text = "Practice-Level Contraindications & Stop Signals",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    aggregatedPracticeContraindications.forEach { item ->
                        Text(
                            text = "• $item",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Criteria for Immediate Medical Referral
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
                        condition.medicalReferralCriteria.forEach { criterion ->
                            Text(
                                text = "• $criterion",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF3E1809),
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }

        // SECTION 4: SUITABLE ASANAS, PRANAYAMA, DHYAN & DIETARY GUIDANCE PREVIEW
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("condition_detail_practices_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Prescribed Yoga & Lifestyle Modules",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${condition.asanas.size} Asanas • ${condition.pranayamas.size} Pranayamas • ${condition.dhyanas.size} Meditations",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(
                        onClick = onNavigateToPracticeLibrary,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Full Practice View",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                val allPractices = remember(condition.id) {
                    condition.asanas + condition.pranayamas + condition.dhyanas
                }

                allPractices.forEach { practice ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStartPractice(practice) }
                            .testTag("condition_detail_practice_${practice.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = when (practice.category) {
                                                PracticeCategory.ASANA -> Icons.Default.SelfImprovement
                                                PracticeCategory.PRANAYAMA -> Icons.Default.Air
                                                PracticeCategory.DHYANA -> Icons.Default.Psychology
                                            },
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${practice.category.label}: ${practice.title}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${practice.sanskritName} • ${practice.durationMinutes} min • ${practice.difficulty}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onStartPractice(practice) },
                                modifier = Modifier.testTag("start_condition_practice_${practice.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Start ${practice.title}",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()

                // Dietary & Lifestyle Guidance Summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestaurantMenu,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Dietary & Lifestyle Guidance",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = condition.dietaryProtocol.doshaBalance,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = onNavigateToDietProtocol,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("condition_detail_diet_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Diet Guide",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}
