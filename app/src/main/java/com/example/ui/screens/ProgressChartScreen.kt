package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.DiseaseCondition
import com.example.data.model.PatientProfile
import com.example.data.model.RecoveryLog
import java.util.Locale
import kotlin.math.abs

enum class SymptomMetricSeries(
    val id: String,
    val label: String,
    val directionHint: String,
    val colorHex: Long
) {
    PAIN("pain", "Pain", "0–10 ↓", 0xFFB85D3B),
    MOOD("mood", "Mood", "1–10 ↑", 0xFF1F4E38),
    STIFFNESS("stiffness", "Stiffness", "0–10 ↓", 0xFFC98A2E),
    MOBILITY("mobility", "Mobility", "1–10 ↑", 0xFF2E7D5B),
    SLEEP("sleep", "Sleep", "1–10 ↑", 0xFF3B6E8C)
}

enum class ChartTimeWindow(val label: String, val maxEntries: Int) {
    LAST_7("7 Logs", 7),
    LAST_14("14 Logs", 14),
    LAST_30("30 Logs", 30),
    ALL("All Time", 90)
}

data class SymptomTrendPoint(
    val dateLabel: String,
    val pain: Int,
    val mood: Int,
    val stiffness: Int,
    val mobility: Int,
    val sleep: Int,
    val asanaMinutes: Int,
    val pranayamaMinutes: Int,
    val dhyanaMinutes: Int,
    val notes: String?
)

fun buildChronologicalTrendPoints(
    logs: List<RecoveryLog>,
    maxEntries: Int
): List<SymptomTrendPoint> {
    return logs
        .take(maxEntries.coerceAtLeast(1))
        .reversed()
        .map { log ->
            SymptomTrendPoint(
                dateLabel = log.dateLabel.ifBlank { "Entry" },
                pain = log.painLevel.coerceIn(0, 10),
                mood = (10 - log.stressLevel.coerceIn(0, 10)).coerceIn(1, 10),
                stiffness = log.stiffnessLevel.coerceIn(0, 10),
                mobility = log.mobilityScore.coerceIn(1, 10),
                sleep = log.sleepQuality.coerceIn(1, 10),
                asanaMinutes = log.asanaMinutes.coerceAtLeast(0),
                pranayamaMinutes = log.pranayamaMinutes.coerceAtLeast(0),
                dhyanaMinutes = log.dhyanaMinutes.coerceAtLeast(0),
                notes = log.notes
            )
        }
}

/**
 * Standalone Jetpack Compose 'Progress Chart' screen that visualizes symptom scores
 * (Pain, Mood, Stiffness, Mobility, Sleep) and daily yoga practice minutes over time
 * based on the [RecoveryLog] records stored in Firestore.
 */
@Composable
fun ProgressChartScreen(
    selectedCondition: DiseaseCondition,
    patientProfile: PatientProfile?,
    recoveryLogs: List<RecoveryLog>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("progress_chart_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ProgressSymptomChartCard(
                selectedCondition = selectedCondition,
                patientProfile = patientProfile,
                recoveryLogs = recoveryLogs
            )
        }
    }
}

/**
 * Reusable interactive Canvas Progress Chart Card visualizing Firestore symptom scores
 * (Pain, Mood, Stiffness, Mobility, Sleep Quality) and daily Yoga/Pranayama/Dhyana minutes over time.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProgressSymptomChartCard(
    selectedCondition: DiseaseCondition,
    patientProfile: PatientProfile?,
    recoveryLogs: List<RecoveryLog>,
    modifier: Modifier = Modifier
) {
    var selectedWindow by rememberSaveable { mutableStateOf(ChartTimeWindow.LAST_14) }
    val activeSeries = remember {
        mutableStateListOf(
            SymptomMetricSeries.PAIN,
            SymptomMetricSeries.MOOD,
            SymptomMetricSeries.STIFFNESS,
            SymptomMetricSeries.MOBILITY
        )
    }

    val baselinePain = patientProfile?.baselinePainScore ?: 6
    val baselineMobility = patientProfile?.baselineMobilityScore ?: 5
    val baselineMood = (10 - (patientProfile?.baselineStressScore ?: 5)).coerceIn(1, 10)

    val points = remember(recoveryLogs, selectedWindow) {
        buildChronologicalTrendPoints(recoveryLogs, selectedWindow.maxEntries)
    }

    var selectedPointIndex by rememberSaveable(points.size) {
        mutableIntStateOf((points.size - 1).coerceAtLeast(0))
    }
    val safeSelectedIdx = selectedPointIndex.coerceIn(0, (points.size - 1).coerceAtLeast(0))
    val inspectedPoint = points.getOrNull(safeSelectedIdx)

    val avgPain = if (points.isNotEmpty()) {
        String.format(Locale.US, "%.1f", points.map { it.pain }.average())
    } else "$baselinePain.0"
    val avgMood = if (points.isNotEmpty()) {
        String.format(Locale.US, "%.1f", points.map { it.mood }.average())
    } else "$baselineMood.0"
    val avgStiffness = if (points.isNotEmpty()) {
        String.format(Locale.US, "%.1f", points.map { it.stiffness }.average())
    } else "5.0"
    val avgMobility = if (points.isNotEmpty()) {
        String.format(Locale.US, "%.1f", points.map { it.mobility }.average())
    } else "$baselineMobility.0"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("progress_symptom_chart_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header & Active Condition Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Symptom & Mood Progress Chart",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Firestore recovery trajectory • ${selectedCondition.shortName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "${points.size} Entries",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // 2. Time Window Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChartTimeWindow.entries.forEach { window ->
                    val selected = selectedWindow == window
                    FilterChip(
                        selected = selected,
                        onClick = { selectedWindow = window },
                        label = {
                            Text(
                                text = window.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("chart_range_${window.name.lowercase()}")
                    )
                }
            }

            // 3. Interactive Metric Series Filter Chips (Pain, Mood, Stiffness, Mobility, Sleep)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SymptomMetricSeries.entries.forEach { series ->
                    val isSelected = series in activeSeries
                    val seriesColor = Color(series.colorHex)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected && activeSeries.size > 1) {
                                activeSeries.remove(series)
                            } else if (!isSelected) {
                                activeSeries.add(series)
                            }
                        },
                        label = {
                            Text(
                                text = "${series.label} (${series.directionHint})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(seriesColor)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = seriesColor.copy(alpha = 0.16f),
                            selectedLabelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.testTag("metric_toggle_${series.id}")
                    )
                }
            }

            // 4. Multi-Series Canvas Symptom & Mood Trajectory Chart
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Score Scale: 0 (Low) to 10 (High)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Tap chart to inspect date",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    SymptomMultiLineCanvasChart(
                        points = points,
                        activeSeries = activeSeries,
                        baselinePain = baselinePain,
                        baselineMood = baselineMood,
                        baselineMobility = baselineMobility,
                        selectedIndex = safeSelectedIdx,
                        onSelectIndex = { selectedPointIndex = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .testTag("symptom_progress_canvas")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // X-axis date labels
                    if (points.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = points.first().dateLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (points.size > 2) {
                                Text(
                                    text = points[points.size / 2].dateLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (points.size > 1) {
                                Text(
                                    text = points.last().dateLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Baseline reference plotted (Pain $baselinePain/10 • Mood $baselineMood/10 • Mobility $baselineMobility/10). Log today's check-in above to plot daily curves.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 5. Inspected Day Snapshot Callout (Interactive Point Details)
            if (inspectedPoint != null) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("inspected_point_summary_card")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Selected Entry: ${inspectedPoint.dateLabel}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${inspectedPoint.asanaMinutes + inspectedPoint.pranayamaMinutes + inspectedPoint.dhyanaMinutes} mins practiced",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ScoreStatPill("Pain", "${inspectedPoint.pain}/10", Color(0xFFB85D3B))
                            ScoreStatPill("Mood", "${inspectedPoint.mood}/10", Color(0xFF1F4E38))
                            ScoreStatPill("Stiffness", "${inspectedPoint.stiffness}/10", Color(0xFFC98A2E))
                            ScoreStatPill("Mobility", "${inspectedPoint.mobility}/10", Color(0xFF2E7D5B))
                            ScoreStatPill("Sleep", "${inspectedPoint.sleep}/10", Color(0xFF3B6E8C))
                        }

                        if (!inspectedPoint.notes.isNullOrBlank()) {
                            Text(
                                text = "Note: ${inspectedPoint.notes}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // 6. Period Averages Summary Row (Pain, Mood, Stiffness, Mobility)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AverageMetricTile(
                    label = "Avg Pain",
                    value = "$avgPain/10",
                    baselineLabel = "Base $baselinePain",
                    color = Color(0xFFB85D3B),
                    modifier = Modifier.weight(1f)
                )
                AverageMetricTile(
                    label = "Avg Mood",
                    value = "$avgMood/10",
                    baselineLabel = "Base $baselineMood",
                    color = Color(0xFF1F4E38),
                    modifier = Modifier.weight(1f)
                )
                AverageMetricTile(
                    label = "Avg Stiff",
                    value = "$avgStiffness/10",
                    baselineLabel = "Target <3",
                    color = Color(0xFFC98A2E),
                    modifier = Modifier.weight(1f)
                )
                AverageMetricTile(
                    label = "Avg Mobil",
                    value = "$avgMobility/10",
                    baselineLabel = "Base $baselineMobility",
                    color = Color(0xFF2E7D5B),
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider()

            // 7. Daily Yoga, Pranayama & Dhyana Practice Minutes Bar Chart (Canvas)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Daily Practice Adherence Minutes (Asana • Pranayama • Dhyan)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                DailyPracticeStackedBarCanvas(
                    points = points,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("practice_minutes_bar_canvas")
                )

                Text(
                    text = "Clinical Transparency: All symptom & mood curves reflect patient self-reported Firestore logs. Adherence is tracked alongside outcomes without claiming yoga alone caused symptom changes.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SymptomMultiLineCanvasChart(
    points: List<SymptomTrendPoint>,
    activeSeries: List<SymptomMetricSeries>,
    baselinePain: Int,
    baselineMood: Int,
    baselineMobility: Int,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .pointerInput(points.size) {
                if (points.size > 1) {
                    detectTapGestures { tapOffset ->
                        val w = size.width.toFloat().coerceAtLeast(1f)
                        val stepX = w / (points.size - 1)
                        val nearestIdx = (0 until points.size).minByOrNull { idx ->
                            abs(idx * stepX - tapOffset.x)
                        } ?: (points.size - 1)
                        onSelectIndex(nearestIdx)
                    }
                }
            }
    ) {
        val leftPad = 14.dp.toPx()
        val rightPad = 14.dp.toPx()
        val topPad = 10.dp.toPx()
        val bottomPad = 10.dp.toPx()
        val chartW = (size.width - leftPad - rightPad).coerceAtLeast(1f)
        val chartH = (size.height - topPad - bottomPad).coerceAtLeast(1f)

        fun yForScore(score: Int): Float {
            val clamped = score.coerceIn(0, 10)
            return topPad + chartH - (clamped / 10f) * chartH
        }

        fun xForIndex(index: Int, count: Int): Float {
            return if (count > 1) {
                leftPad + (index.toFloat() / (count - 1)) * chartW
            } else {
                leftPad + chartW / 2f
            }
        }

        // Draw horizontal grid lines at 0, 2, 5, 8, 10
        listOf(0, 2, 5, 8, 10).forEach { level ->
            val y = yForScore(level)
            drawLine(
                color = Color(0xFFCBD8D0).copy(alpha = if (level == 5) 0.85f else 0.45f),
                start = Offset(leftPad, y),
                end = Offset(leftPad + chartW, y),
                strokeWidth = if (level == 5) 1.5.dp.toPx() else 1.dp.toPx()
            )
        }

        // Draw dashed baseline pain reference line
        val basePainY = yForScore(baselinePain)
        drawLine(
            color = Color(0xFFB85D3B).copy(alpha = 0.35f),
            start = Offset(leftPad, basePainY),
            end = Offset(leftPad + chartW, basePainY),
            strokeWidth = 1.2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
        )

        if (points.isEmpty()) {
            val midX = leftPad + chartW / 2f
            drawCircle(
                color = Color(0xFFB85D3B),
                radius = 6.dp.toPx(),
                center = Offset(midX - 24.dp.toPx(), yForScore(baselinePain))
            )
            drawCircle(
                color = Color(0xFF1F4E38),
                radius = 6.dp.toPx(),
                center = Offset(midX, yForScore(baselineMood))
            )
            drawCircle(
                color = Color(0xFF2E7D5B),
                radius = 6.dp.toPx(),
                center = Offset(midX + 24.dp.toPx(), yForScore(baselineMobility))
            )
            return@Canvas
        }

        // Highlight selected vertical cursor line
        val safeSelected = selectedIndex.coerceIn(0, points.size - 1)
        val selectedX = xForIndex(safeSelected, points.size)
        drawLine(
            color = Color(0xFF1F4E38).copy(alpha = 0.28f),
            start = Offset(selectedX, topPad),
            end = Offset(selectedX, topPad + chartH),
            strokeWidth = 2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
        )

        fun valueForSeries(point: SymptomTrendPoint, series: SymptomMetricSeries): Int = when (series) {
            SymptomMetricSeries.PAIN -> point.pain
            SymptomMetricSeries.MOOD -> point.mood
            SymptomMetricSeries.STIFFNESS -> point.stiffness
            SymptomMetricSeries.MOBILITY -> point.mobility
            SymptomMetricSeries.SLEEP -> point.sleep
        }

        // Draw smooth paths and area fill for each active series
        activeSeries.forEachIndexed { seriesIdx, series ->
            val seriesColor = Color(series.colorHex)
            val coords = points.mapIndexed { idx, pt ->
                Offset(xForIndex(idx, points.size), yForScore(valueForSeries(pt, series)))
            }

            if (coords.size > 1) {
                val linePath = Path().apply {
                    moveTo(coords.first().x, coords.first().y)
                    for (i in 1 until coords.size) {
                        val prev = coords[i - 1]
                        val curr = coords[i]
                        val controlX = (prev.x + curr.x) / 2f
                        cubicTo(controlX, prev.y, controlX, curr.y, curr.x, curr.y)
                    }
                }

                // Draw subtle gradient fill under the primary series
                if (seriesIdx == 0) {
                    val fillPath = Path().apply {
                        addPath(linePath)
                        lineTo(coords.last().x, topPad + chartH)
                        lineTo(coords.first().x, topPad + chartH)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                seriesColor.copy(alpha = 0.22f),
                                seriesColor.copy(alpha = 0.02f)
                            ),
                            startY = topPad,
                            endY = topPad + chartH
                        )
                    )
                }

                drawPath(
                    path = linePath,
                    color = seriesColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Draw data nodes
            coords.forEachIndexed { idx, offset ->
                val isSelectedNode = idx == safeSelected
                if (isSelectedNode) {
                    drawCircle(
                        color = seriesColor.copy(alpha = 0.25f),
                        radius = 9.dp.toPx(),
                        center = offset
                    )
                }
                drawCircle(
                    color = Color.White,
                    radius = if (isSelectedNode) 5.5.dp.toPx() else 4.dp.toPx(),
                    center = offset
                )
                drawCircle(
                    color = seriesColor,
                    radius = if (isSelectedNode) 4.dp.toPx() else 3.dp.toPx(),
                    center = offset
                )
            }
        }
    }
}

@Composable
fun DailyPracticeStackedBarCanvas(
    points: List<SymptomTrendPoint>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        if (points.isEmpty()) {
            drawRoundRect(
                color = Color(0xFFE3ECE6),
                topLeft = Offset(0f, h * 0.4f),
                size = Size(w, h * 0.35f),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
            return@Canvas
        }

        val maxTotalMins = points.maxOf {
            (it.asanaMinutes + it.pranayamaMinutes + it.dhyanaMinutes).coerceAtLeast(30)
        }.toFloat()

        val slotWidth = w / points.size.coerceAtLeast(1)
        val barWidth = (slotWidth * 0.55f).coerceIn(8.dp.toPx(), 28.dp.toPx())

        points.forEachIndexed { idx, pt ->
            val centerX = idx * slotWidth + slotWidth / 2f
            val leftX = centerX - barWidth / 2f

            val asanaH = (pt.asanaMinutes / maxTotalMins) * (h * 0.85f)
            val pranaH = (pt.pranayamaMinutes / maxTotalMins) * (h * 0.85f)
            val dhyanaH = (pt.dhyanaMinutes / maxTotalMins) * (h * 0.85f)

            var currentBottom = h
            if (asanaH > 0f) {
                drawRoundRect(
                    color = Color(0xFF1F4E38),
                    topLeft = Offset(leftX, currentBottom - asanaH),
                    size = Size(barWidth, asanaH),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                currentBottom -= asanaH
            }
            if (pranaH > 0f) {
                drawRoundRect(
                    color = Color(0xFFC98A2E),
                    topLeft = Offset(leftX, currentBottom - pranaH),
                    size = Size(barWidth, pranaH),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                currentBottom -= pranaH
            }
            if (dhyanaH > 0f) {
                drawRoundRect(
                    color = Color(0xFFB85D3B),
                    topLeft = Offset(leftX, currentBottom - dhyanaH),
                    size = Size(barWidth, dhyanaH),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }
    }
}

@Composable
private fun ScoreStatPill(
    label: String,
    value: String,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.14f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AverageMetricTile(
    label: String,
    value: String,
    baselineLabel: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = baselineLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
