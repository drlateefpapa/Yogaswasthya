package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.catalog.DiseaseCatalog
import com.example.data.catalog.MeditationCatalog
import com.example.data.model.DiseaseCondition
import com.example.data.model.MeditationTrack
import com.example.data.model.PracticeCategory
import com.example.data.model.TherapeuticPractice

data class PracticeLibraryEntry(
    val practice: TherapeuticPractice,
    val movementCategory: String,
    val difficultyTier: String,
    val shortDescription: String,
    val conditionIds: List<String>,
    val conditionShortNames: List<String>,
    val primaryAccentHex: Long
)

val LibraryCategoryOptions = listOf(
    "All Categories",
    "Asana: Spinal & Neck",
    "Asana: Joint & Hip/Knee",
    "Asana: Supine & Restorative",
    "Pranayama Breathwork",
    "Dhyan & Relaxation"
)

val LibraryDifficultyOptions = listOf(
    "All Difficulties",
    "Beginner",
    "Gentle / Restorative",
    "Chair / Senior Friendly"
)

fun deriveMovementCategory(practice: TherapeuticPractice): String {
    return when (practice.category) {
        PracticeCategory.PRANAYAMA -> "Pranayama Breathwork"
        PracticeCategory.DHYANA -> "Dhyan & Relaxation"
        PracticeCategory.ASANA -> {
            val combined = "${practice.title} ${practice.anatomicalFocus} ${practice.startingPosition}".lowercase()
            when {
                combined.contains("supine") || combined.contains("corpse") ||
                    combined.contains("shavasana") || combined.contains("legs-up") ||
                    combined.contains("viparita") || combined.contains("restorative") ||
                    combined.contains("child") || combined.contains("balasana") ->
                    "Asana: Supine & Restorative"

                combined.contains("knee") || combined.contains("hip") ||
                    combined.contains("shoulder") || combined.contains("quadriceps") ||
                    combined.contains("ankle") || combined.contains("tadasana") ||
                    combined.contains("bridge") || combined.contains("setu") ->
                    "Asana: Joint & Hip/Knee"

                else -> "Asana: Spinal & Neck"
            }
        }
    }
}

fun deriveDifficultyTier(practice: TherapeuticPractice): String {
    val diffLower = practice.difficulty.lowercase()
    val modLower = "${practice.title} ${practice.difficulty} ${practice.chairAndWallModifications}".lowercase()
    return when {
        diffLower.contains("restorative") || diffLower.contains("gentle") || diffLower.contains("all levels") ->
            "Gentle / Restorative"
        modLower.contains("chair") || modLower.contains("wall") || diffLower.contains("senior") ->
            "Chair / Senior Friendly"
        else -> "Beginner"
    }
}

fun buildPracticeLibraryEntries(
    conditions: List<DiseaseCondition> = DiseaseCatalog.conditions
): List<PracticeLibraryEntry> {
    val grouped = linkedMapOf<String, MutableList<Pair<DiseaseCondition, TherapeuticPractice>>>()
    conditions.forEach { condition ->
        (condition.asanas + condition.pranayamas + condition.dhyanas).forEach { practice ->
            grouped.getOrPut(practice.id) { mutableListOf() }.add(condition to practice)
        }
    }

    return grouped.map { (_, pairs) ->
        val firstCondition = pairs.first().first
        val practice = pairs.first().second
        val conditionIds = pairs.map { it.first.id }.distinct()
        val conditionShortNames = pairs.map { it.first.shortName }.distinct()
        val benefitSummary = practice.therapeuticBenefits.firstOrNull()
            ?: "Supports ${practice.anatomicalFocus}"
        val shortDesc = "${practice.anatomicalFocus}. $benefitSummary"

        PracticeLibraryEntry(
            practice = practice,
            movementCategory = deriveMovementCategory(practice),
            difficultyTier = deriveDifficultyTier(practice),
            shortDescription = shortDesc,
            conditionIds = conditionIds,
            conditionShortNames = conditionShortNames,
            primaryAccentHex = firstCondition.accentHex
        )
    }
}

/**
 * Jetpack Compose 'PracticeLibraryScreen' that displays a searchable, filterable catalog
 * of yoga asanas, screened pranayama, and dhyan practices with categories, difficulty filters,
 * condition filters, illustrated posture cards, short descriptions, and expandable instructions.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PracticeLibraryScreen(
    selectedCondition: DiseaseCondition,
    availableConditions: List<DiseaseCondition> = DiseaseCatalog.conditions,
    onSelectCondition: (String) -> Unit = {},
    onStartPractice: (TherapeuticPractice) -> Unit = {},
    onStartMeditationTrack: (MeditationTrack, Int) -> Unit = { _, _ -> },
    onNavigateToDiet: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val allEntries = remember(availableConditions) {
        buildPracticeLibraryEntries(availableConditions)
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("All Categories") }
    var selectedDifficulty by rememberSaveable { mutableStateOf("All Difficulties") }
    // Default to "all" so users can browse the full library or filter to any specific condition
    var selectedConditionFilterId by rememberSaveable { mutableStateOf("all") }

    val filteredEntries = remember(
        allEntries,
        searchQuery,
        selectedCategory,
        selectedDifficulty,
        selectedConditionFilterId
    ) {
        allEntries.filter { entry ->
            val matchesCategory = selectedCategory == "All Categories" ||
                entry.movementCategory.equals(selectedCategory, ignoreCase = true)

            val matchesDifficulty = when (selectedDifficulty) {
                "All Difficulties" -> true
                "Beginner" -> entry.difficultyTier == "Beginner" ||
                    entry.practice.difficulty.contains("Beginner", ignoreCase = true)
                "Gentle / Restorative" -> entry.difficultyTier == "Gentle / Restorative" ||
                    entry.practice.difficulty.contains("Gentle", ignoreCase = true) ||
                    entry.practice.difficulty.contains("Restorative", ignoreCase = true) ||
                    entry.practice.difficulty.contains("All Levels", ignoreCase = true)
                "Chair / Senior Friendly" -> entry.difficultyTier == "Chair / Senior Friendly" ||
                    entry.practice.chairAndWallModifications.isNotBlank()
                else -> true
            }

            val matchesCondition = selectedConditionFilterId == "all" ||
                selectedConditionFilterId in entry.conditionIds

            val q = searchQuery.trim().lowercase()
            val matchesSearch = q.isBlank() ||
                entry.practice.title.lowercase().contains(q) ||
                entry.practice.sanskritName.lowercase().contains(q) ||
                entry.practice.anatomicalFocus.lowercase().contains(q) ||
                entry.shortDescription.lowercase().contains(q) ||
                entry.conditionShortNames.any { it.lowercase().contains(q) }

            matchesCategory && matchesDifficulty && matchesCondition && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("practice_library_screen"),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Banner & Active Condition Quick Filter
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF163828)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0x33F3C878),
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SelfImprovement,
                                    contentDescription = null,
                                    tint = Color(0xFFF3C878),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "THERAPEUTIC ASANA & PRACTICE LIBRARY",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFFBF8F3),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            color = Color.White.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "${filteredEntries.size} of ${allEntries.size} Practices",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFBF8F3),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "Yoga Asanas, Pranayama & Dhyan Library",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFFFBF8F3),
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Filter by movement category, difficulty level, or health condition. Every card includes posture visuals, functional benefits, chair/wall modifications, and safety stop signals.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFDCEBE3)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                selectedConditionFilterId = if (selectedConditionFilterId == selectedCondition.id) {
                                    "all"
                                } else {
                                    selectedCondition.id
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFBF8F3),
                                contentColor = Color(0xFF143325)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("toggle_active_condition_filter_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (selectedConditionFilterId == selectedCondition.id)
                                    "Showing: ${selectedCondition.shortName} (Tap for All)"
                                else
                                    "Filter to ${selectedCondition.shortName}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateToDiet,
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFFBF8F3)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("practice_library_diet_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestaurantMenu,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Diet Guide",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

        // 2. Search & Multi-Dimensional Filters Card (Category, Difficulty, Condition)
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
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text("Search Asanas, Sanskrit name, muscle area, or symptom...")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search practices"
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
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("practice_library_search_input")
                    )

                    // Filter 1: Category Filter Row
                    Text(
                        text = "Category:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LibraryCategoryOptions.forEach { category ->
                            val isSelected = selectedCategory == category
                            val safeTag = category.lowercase()
                                .replace(":", "")
                                .replace("/", "_")
                                .replace("&", "")
                                .trim()
                                .replace(Regex("\\s+"), "_")
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = category },
                                label = {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.testTag("practice_category_chip_$safeTag")
                            )
                        }
                    }

                    // Filter 2: Difficulty Filter Row
                    Text(
                        text = "Difficulty & Mobility Level:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LibraryDifficultyOptions.forEach { difficulty ->
                            val isSelected = selectedDifficulty == difficulty
                            val safeTag = difficulty.lowercase()
                                .replace("/", "_")
                                .trim()
                                .replace(Regex("\\s+"), "_")
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedDifficulty = difficulty },
                                label = {
                                    Text(
                                        text = difficulty,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                modifier = Modifier.testTag("practice_difficulty_chip_$safeTag")
                            )
                        }
                    }

                    // Filter 3: Health Condition Filter Row
                    Text(
                        text = "Health Condition Filter:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedConditionFilterId == "all",
                            onClick = { selectedConditionFilterId = "all" },
                            label = {
                                Text(
                                    text = "All Conditions (${availableConditions.size})",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (selectedConditionFilterId == "all") FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.testTag("practice_condition_chip_all")
                        )

                        availableConditions.forEach { condition ->
                            val isSelected = selectedConditionFilterId == condition.id
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedConditionFilterId = condition.id
                                    onSelectCondition(condition.id)
                                },
                                label = {
                                    Text(
                                        text = condition.shortName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.testTag("practice_condition_chip_${condition.id}")
                            )
                        }
                    }
                }
            }
        }

        // 3. Empty Filter State
        if (filteredEntries.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
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
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Matching Practices Found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try resetting your category, difficulty, or condition filter to view all therapeutic yoga practices.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                searchQuery = ""
                                selectedCategory = "All Categories"
                                selectedDifficulty = "All Difficulties"
                                selectedConditionFilterId = "all"
                            },
                            modifier = Modifier.testTag("reset_practice_filters_button")
                        ) {
                            Text("Reset All Filters")
                        }
                    }
                }
            }
        }

        // 4. Practice Cards with Posture Illustrations, Categories, Short Descriptions & Expandable Instructions
        items(filteredEntries, key = { it.practice.id }) { entry ->
            PracticeLibraryItemCard(
                entry = entry,
                onStartPractice = { onStartPractice(entry.practice) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 5. Guided Meditation & Yoga Nidra Studio Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Guided Meditation (Dhyan) & Yoga Nidra Studio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Multi-duration (3, 5, 10, 15 min) body scans & breath-focused relaxation",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    MeditationCatalog.tracks.forEach { track ->
                        var selectedTrackMins by rememberSaveable(track.id) {
                            mutableIntStateOf(track.availableDurationsMinutes.getOrElse(1) { 5 })
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = track.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${track.subtitle} • ${track.ambientSoundLabel}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Button(
                                        onClick = { onStartMeditationTrack(track, selectedTrackMins) },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("start_meditation_track_${track.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${selectedTrackMins}m")
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    track.availableDurationsMinutes.forEach { mins ->
                                        FilterChip(
                                            selected = selectedTrackMins == mins,
                                            onClick = { selectedTrackMins = mins },
                                            label = {
                                                Text(
                                                    text = "$mins min",
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Reusable Practice Library Card featuring:
 * - Visual banner header with custom posture illustration & vector Image
 * - Movement category & difficulty badges
 * - Sanskrit & English titles + short functional description
 * - Applicable condition tags
 * - Expandable clinical step-by-step instructions, chair/wall modifications & stop signals
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PracticeLibraryItemCard(
    entry: PracticeLibraryEntry,
    onStartPractice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val practice = entry.practice
    var expanded by rememberSaveable(practice.id) { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("practice_library_card_${practice.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Card Image & Posture Illustration Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(138.dp)
                    .clickable { expanded = !expanded }
                    .testTag("practice_card_image_${practice.id}")
            ) {
                AsanaPostureVisualBanner(
                    category = practice.category,
                    accentColor = Color(entry.primaryAccentHex),
                    modifier = Modifier.fillMaxSize()
                )

                // Vector Posture Illustration Image on the right
                Image(
                    painter = painterResource(id = R.drawable.ic_asana_posture_silhouette),
                    contentDescription = "${practice.title} posture illustration",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 18.dp)
                        .size(96.dp)
                )

                // Top Badges Row (Movement Category + Duration)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.32f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = entry.movementCategory,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFBF8F3),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = Color(0xFFD4A359),
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = Color(0xFF18241E),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${practice.durationMinutes} min",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF18241E),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Bottom Left Difficulty & Chair Support Pill
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFFFBF8F3).copy(alpha = 0.92f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${entry.difficultyTier} • ${practice.difficulty}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF143325),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // 2. Card Body: Title, Sanskrit Name, Short Description, Condition Tags & Actions
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = practice.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${practice.sanskritName} • ${practice.holdAndReps}",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.testTag("expand_practice_card_${practice.id}")
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) "Collapse details" else "Expand details"
                        )
                    }
                }

                // Short Description
                Text(
                    text = entry.shortDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("practice_card_description_${practice.id}")
                )

                // Prescribed Conditions Tags
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    entry.conditionShortNames.take(4).forEach { condName ->
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = condName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    if (entry.conditionShortNames.size > 4) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "+${entry.conditionShortNames.size - 4} more",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Expandable Full Asana Instruction Module (Section 5)
                AnimatedVisibility(visible = expanded) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HorizontalDivider()

                        Text(
                            text = "Muscles & Movement Areas: ${practice.anatomicalFocus}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Starting Position: ${practice.startingPosition}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Breathing Cue: ${practice.breathingCue}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Text(
                            text = "Step-by-Step Instructions:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        practice.stepByStepInstructions.forEachIndexed { idx, step ->
                            Text(
                                text = "${idx + 1}. $step",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (practice.therapeuticBenefits.isNotEmpty()) {
                            Text(
                                text = "Expected Functional Benefits:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            practice.therapeuticBenefits.forEach { benefit ->
                                Text(
                                    text = "• $benefit",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessibilityNew,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Chair & Wall Modification: ${practice.chairAndWallModifications}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }

                        if (practice.commonMistakes.isNotEmpty()) {
                            Text(
                                text = "Common Mistakes to Avoid:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            practice.commonMistakes.forEach { mistake ->
                                Text(
                                    text = "• $mistake",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "Safely Exiting the Pose: ${practice.safeExitInstructions}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (practice.contraindications.isNotEmpty() || practice.stopSignals.isNotEmpty()) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.WarningAmber,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Contraindications & Stop Signals",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    practice.contraindications.forEach { warn ->
                                        Text(
                                            text = "• Contraindication: $warn",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                    practice.stopSignals.forEach { signal ->
                                        Text(
                                            text = "• Stop Signal: $signal",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { expanded = !expanded },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (expanded) "Hide Steps" else "Steps & Safety",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Button(
                        onClick = onStartPractice,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("start_library_practice_${practice.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Start Guided (${practice.durationMinutes}m)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom Jetpack Compose Canvas background illustration for Asana, Pranayama, and Dhyana cards.
 */
@Composable
fun AsanaPostureVisualBanner(
    category: PracticeCategory,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val baseColors = when (category) {
            PracticeCategory.ASANA -> listOf(Color(0xFF143325), accentColor, Color(0xFF285943))
            PracticeCategory.PRANAYAMA -> listOf(Color(0xFF1B3A4B), accentColor, Color(0xFF2E5B52))
            PracticeCategory.DHYANA -> listOf(Color(0xFF2A2438), accentColor, Color(0xFF1F4E38))
        }

        drawRect(
            brush = Brush.linearGradient(
                colors = baseColors,
                start = Offset(0f, 0f),
                end = Offset(w, h)
            )
        )

        // Subtle sacred geometry aura behind the right-aligned posture icon
        val auraCenter = Offset(w * 0.82f, h * 0.5f)
        for (i in 1..3) {
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = h * (0.24f + i * 0.14f),
                center = auraCenter,
                style = Stroke(width = 2f)
            )
        }

        // Flowing breath wave across bottom
        val wavePath = Path().apply {
            moveTo(0f, h * 0.78f)
            quadraticBezierTo(w * 0.3f, h * 0.58f, w * 0.62f, h * 0.76f)
            quadraticBezierTo(w * 0.84f, h * 0.88f, w, h * 0.68f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = wavePath,
            color = Color.Black.copy(alpha = 0.18f)
        )

        // Spine alignment or prana flow line accent
        drawLine(
            color = Color(0x55F3C878),
            start = Offset(w * 0.06f, h * 0.52f),
            end = Offset(w * 0.58f, h * 0.52f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
