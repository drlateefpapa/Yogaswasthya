package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = MintGlowPrimary,
    onPrimary = Color(0xFF072918),
    primaryContainer = Color(0xFF1D4632),
    onPrimaryContainer = Color(0xFFD9EDE2),
    secondary = GoldenLotusSecondary,
    onSecondary = Color(0xFF382405),
    secondaryContainer = Color(0xFF523810),
    onSecondaryContainer = Color(0xFFFFE2B8),
    tertiary = SoftTerracottaTertiary,
    onTertiary = Color(0xFF42190A),
    tertiaryContainer = Color(0xFF5E2A17),
    onTertiaryContainer = Color(0xFFFCE6DC),
    background = DarkBotanicalBg,
    onBackground = Color(0xFFE8EFEA),
    surface = DarkBotanicalSurface,
    onSurface = Color(0xFFE8EFEA),
    surfaceVariant = DarkBotanicalCard,
    onSurfaceVariant = Color(0xFFB8C9BF),
    outline = Color(0xFF3E5448)
)

private val LightColorScheme = lightColorScheme(
    primary = ForestSagePrimary,
    onPrimary = Color.White,
    primaryContainer = SageMintContainer,
    onPrimaryContainer = OnSageMintContainer,
    secondary = WarmSaffronGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFAEBD2),
    onSecondaryContainer = Color(0xFF3A2506),
    tertiary = WarmTerracotta,
    onTertiary = Color.White,
    tertiaryContainer = TerracottaContainer,
    onTertiaryContainer = OnTerracottaContainer,
    background = ParchmentCream,
    onBackground = DeepBotanicalInk,
    surface = SoftAlabasterCard,
    onSurface = DeepBotanicalInk,
    surfaceVariant = WarmSandSurface,
    onSurfaceVariant = MutedHerbGray,
    outline = SubtleSageOutline
)

val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
