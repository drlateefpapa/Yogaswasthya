package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.RecoveryLog
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SacredLotusEmblem(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "lotus_glow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f

        // Outer golden prana aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x66D4A359),
                    Color(0x22D4A359),
                    Color.Transparent
                ),
                center = center,
                radius = radius * pulse
            ),
            radius = radius * pulse,
            center = center
        )

        // Inner deep emerald medallion
        drawCircle(
            color = Color(0xFF1B3B2B),
            radius = radius * 0.76f,
            center = center
        )
        drawCircle(
            color = Color(0xFFD4A359),
            radius = radius * 0.76f,
            center = center,
            style = Stroke(width = 3f)
        )

        // 8-petal sacred lotus geometry
        for (i in 0 until 8) {
            rotate(degrees = i * 45f, pivot = center) {
                val petalPath = Path().apply {
                    moveTo(center.x, center.y - radius * 0.12f)
                    quadraticBezierTo(
                        center.x - radius * 0.18f,
                        center.y - radius * 0.42f,
                        center.x,
                        center.y - radius * 0.62f
                    )
                    quadraticBezierTo(
                        center.x + radius * 0.18f,
                        center.y - radius * 0.42f,
                        center.x,
                        center.y - radius * 0.12f
                    )
                    close()
                }
                drawPath(
                    path = petalPath,
                    color = if (i % 2 == 0) Color(0xFFF5EFE6) else Color(0xFFE8B76B).copy(alpha = 0.85f)
                )
            }
        }

        // Central bindu
        drawCircle(
            color = Color(0xFFD4A359),
            radius = radius * 0.12f,
            center = center
        )
    }
}

@Composable
fun TherapeuticHeroBannerIllustration(
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Warm sunrise botanical sky gradient
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF143325),
                    accentColor,
                    Color(0xFF2B5B44)
                ),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            )
        )

        // Glowing golden prana sun on right side
        val sunCenter = Offset(w * 0.82f, h * 0.34f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x88F3C878),
                    Color(0x33D4A359),
                    Color.Transparent
                ),
                center = sunCenter,
                radius = h * 0.75f
            ),
            radius = h * 0.75f,
            center = sunCenter
        )
        drawCircle(
            color = Color(0x55F8E2B0),
            radius = h * 0.22f,
            center = sunCenter
        )

        // Concentric sacred geometry waves
        for (i in 1..3) {
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = h * (0.28f + i * 0.16f),
                center = sunCenter,
                style = Stroke(width = 2f)
            )
        }

        // Layered botanical hills at bottom
        val backHill = Path().apply {
            moveTo(0f, h * 0.78f)
            quadraticBezierTo(w * 0.28f, h * 0.56f, w * 0.62f, h * 0.74f)
            quadraticBezierTo(w * 0.84f, h * 0.85f, w, h * 0.66f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(backHill, color = Color(0x330B1E15))

        val frontHill = Path().apply {
            moveTo(0f, h * 0.88f)
            quadraticBezierTo(w * 0.35f, h * 0.72f, w * 0.75f, h * 0.86f)
            quadraticBezierTo(w * 0.90f, h * 0.91f, w, h * 0.82f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(frontHill, color = Color(0x55081710))
    }
}

@Composable
fun InteractiveBreathMandala(
    phaseLabel: String,
    secondsRemainingInCycle: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breath_mandala")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.62f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath_scale"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.minDimension / 2f

            // Outer ripple rings
            drawCircle(
                color = Color(0xFF1F4E38).copy(alpha = 0.14f),
                radius = maxRadius * 0.96f,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = Color(0xFFC98A2E).copy(alpha = 0.22f),
                radius = maxRadius * scale,
                center = center
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1F4E38),
                        Color(0xFF347456)
                    ),
                    center = center,
                    radius = maxRadius * scale * 0.82f
                ),
                radius = maxRadius * scale * 0.82f,
                center = center
            )

            // 12 prana rays
            for (deg in 0 until 360 step 30) {
                val rad = Math.toRadians(deg.toDouble())
                val innerR = maxRadius * scale * 0.84f
                val outerR = maxRadius * scale * 0.96f
                drawLine(
                    color = Color(0xFFD4A359).copy(alpha = 0.7f),
                    start = Offset(
                        (center.x + innerR * cos(rad)).toFloat(),
                        (center.y + innerR * sin(rad)).toFloat()
                    ),
                    end = Offset(
                        (center.x + outerR * cos(rad)).toFloat(),
                        (center.y + outerR * sin(rad)).toFloat()
                    ),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = phaseLabel,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFFBF8F3),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${secondsRemainingInCycle}s",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFF3C878),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RecoveryTrajectoryChart(
    logs: List<RecoveryLog>,
    baselinePain: Int,
    baselineMobility: Int,
    modifier: Modifier = Modifier
) {
    val chronological = logs.take(10).reversed()
    val painColor = Color(0xFFB85D3B)
    val mobilityColor = Color(0xFF1F4E38)
    val stiffnessColor = Color(0xFFC98A2E)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendDot(color = painColor, label = "Pain (0–10 ↓)")
            LegendDot(color = stiffnessColor, label = "Stiffness (0–10 ↓)")
            LegendDot(color = mobilityColor, label = "Mobility (1–10 ↑)")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            val w = size.width
            val h = size.height

            // Horizontal reference lines (0, 5, 10)
            for (level in listOf(0, 5, 10)) {
                val y = h - (level / 10f) * h
                drawLine(
                    color = Color(0xFFCBD8D0).copy(alpha = 0.6f),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            if (chronological.isEmpty()) {
                // Plot baseline reference points if no daily logs yet
                val painY = h - (baselinePain.coerceIn(0, 10) / 10f) * h
                val mobY = h - (baselineMobility.coerceIn(1, 10) / 10f) * h
                drawCircle(color = painColor, radius = 6.dp.toPx(), center = Offset(w / 2f, painY))
                drawCircle(color = mobilityColor, radius = 6.dp.toPx(), center = Offset(w / 2f, mobY))
                return@Canvas
            }

            val stepX = if (chronological.size > 1) w / (chronological.size - 1) else w / 2f

            fun pointFor(index: Int, value: Int): Offset {
                val x = if (chronological.size > 1) index * stepX else w / 2f
                val y = h - (value.coerceIn(0, 10) / 10f) * h
                return Offset(x, y)
            }

            // Draw lines
            if (chronological.size > 1) {
                val painPath = Path()
                val stiffPath = Path()
                val mobPath = Path()
                chronological.forEachIndexed { idx, log ->
                    val pPt = pointFor(idx, log.painLevel)
                    val sPt = pointFor(idx, log.stiffnessLevel)
                    val mPt = pointFor(idx, log.mobilityScore)
                    if (idx == 0) {
                        painPath.moveTo(pPt.x, pPt.y)
                        stiffPath.moveTo(sPt.x, sPt.y)
                        mobPath.moveTo(mPt.x, mPt.y)
                    } else {
                        painPath.lineTo(pPt.x, pPt.y)
                        stiffPath.lineTo(sPt.x, sPt.y)
                        mobPath.lineTo(mPt.x, mPt.y)
                    }
                }
                drawPath(painPath, color = painColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                drawPath(stiffPath, color = stiffnessColor, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round))
                drawPath(mobPath, color = mobilityColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
            }

            // Draw nodes
            chronological.forEachIndexed { idx, log ->
                drawCircle(
                    color = painColor,
                    radius = 5.dp.toPx(),
                    center = pointFor(idx, log.painLevel)
                )
                drawCircle(
                    color = stiffnessColor,
                    radius = 4.dp.toPx(),
                    center = pointFor(idx, log.stiffnessLevel)
                )
                drawCircle(
                    color = mobilityColor,
                    radius = 5.dp.toPx(),
                    center = pointFor(idx, log.mobilityScore)
                )
            }
        }

        if (chronological.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = chronological.first().dateLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (chronological.size > 1) {
                    Text(
                        text = chronological.last().dateLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(10.dp),
            shape = CircleShape,
            color = color
        ) {}
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PillarMinutesBar(
    asanaMins: Int,
    pranayamaMins: Int,
    dhyanaMins: Int,
    modifier: Modifier = Modifier
) {
    val total = (asanaMins + pranayamaMins + dhyanaMins).coerceAtLeast(1)
    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
        ) {
            val w = size.width
            val h = size.height
            val asanaW = w * (asanaMins.toFloat() / total)
            val pranaW = w * (pranayamaMins.toFloat() / total)
            val dhyanaW = w - asanaW - pranaW

            drawRoundRect(
                color = Color(0xFFE3ECE6),
                size = Size(w, h),
                cornerRadius = CornerRadius(h / 2, h / 2)
            )
            if (asanaW > 0f) {
                drawRoundRect(
                    color = Color(0xFF1F4E38),
                    topLeft = Offset(0f, 0f),
                    size = Size(asanaW, h),
                    cornerRadius = CornerRadius(h / 2, h / 2)
                )
            }
            if (pranaW > 0f) {
                drawRect(
                    color = Color(0xFFC98A2E),
                    topLeft = Offset(asanaW, 0f),
                    size = Size(pranaW, h)
                )
            }
            if (dhyanaW > 0f && dhyanaMins > 0) {
                drawRoundRect(
                    color = Color(0xFFB85D3B),
                    topLeft = Offset(asanaW + pranaW, 0f),
                    size = Size(dhyanaW, h),
                    cornerRadius = CornerRadius(h / 2, h / 2)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Asana: ${asanaMins}m",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF1F4E38),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Pranayama: ${pranayamaMins}m",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFC98A2E),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Dhyana: ${dhyanaMins}m",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFB85D3B),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
