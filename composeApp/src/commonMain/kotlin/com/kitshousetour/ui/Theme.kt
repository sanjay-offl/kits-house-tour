package com.kitshousetour.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import com.kitshousetour.engine.KitMood
import kotlin.math.pow

/** The single dark palette used across Kit's House Tour. */
object KitPalette {
    val Background = Color(0xFF0B0B0F)
    val Surface = Color(0xFF16161D)
    val Border = Color.White.copy(alpha = 0.12f)
    val Text = Color(0xFFFFFFFF)
    val SecondaryText = Color(0xFFB3B3B3)
    val Accent = Color(0xFF8B5CF6)
    val AccentHighlight = Color(0xFFC4B5FD)
    val AccentPressed = Color(0xFF5B21B6)
    val OnAccent = Background
    val OnAccentPressed = Text
    val KitHappy = Accent
    val KitCurious = AccentHighlight
    val KitTired = SecondaryText
    val KitProud = AccentHighlight
    val KitFace = Background
    val KitCheek = AccentHighlight
}

/** WCAG contrast ratios for the foreground and background pairs used by the interface. */
object KitPaletteContrast {
    val TextOnBackground = contrastRatio(KitPalette.Text, KitPalette.Background)
    val TextOnSurface = contrastRatio(KitPalette.Text, KitPalette.Surface)
    val SecondaryTextOnBackground = contrastRatio(KitPalette.SecondaryText, KitPalette.Background)
    val SecondaryTextOnSurface = contrastRatio(KitPalette.SecondaryText, KitPalette.Surface)
    val AccentOnBackground = contrastRatio(KitPalette.Accent, KitPalette.Background)
    val HighlightOnBackground = contrastRatio(KitPalette.AccentHighlight, KitPalette.Background)
    val HighlightOnSurface = contrastRatio(KitPalette.AccentHighlight, KitPalette.Surface)
    val OnAccentOnAccent = contrastRatio(KitPalette.OnAccent, KitPalette.Accent)
    val OnBackgroundOnHighlight =
        contrastRatio(KitPalette.Background, KitPalette.AccentHighlight)
    val OnPressedAccentOnPressedAccent =
        contrastRatio(KitPalette.OnAccentPressed, KitPalette.AccentPressed)
}

/** Resolves the shared mascot color for a mood. */
fun kitColorForMood(mood: KitMood): Color = when (mood) {
    KitMood.Happy -> KitPalette.KitHappy
    KitMood.Curious -> KitPalette.KitCurious
    KitMood.Tired -> KitPalette.KitTired
    KitMood.Proud -> KitPalette.KitProud
}

/** Calculates the WCAG contrast ratio between two opaque colors. */
fun contrastRatio(foreground: Color, background: Color): Double {
    val foregroundLuminance = relativeLuminance(foreground.toArgbLong())
    val backgroundLuminance = relativeLuminance(background.toArgbLong())
    val lighter = maxOf(foregroundLuminance, backgroundLuminance)
    val darker = minOf(foregroundLuminance, backgroundLuminance)
    return (lighter + 0.05) / (darker + 0.05)
}

private fun Color.toArgbLong(): Long = toArgb().toLong() and 0xFFFFFFFFL

private fun relativeLuminance(argb: Long): Double {
    fun channel(shift: Int): Double {
        val value = ((argb shr shift) and 0xFF).toDouble() / 255.0
        return if (value <= 0.04045) value / 12.92 else ((value + 0.055) / 1.055).pow(2.4)
    }

    return 0.2126 * channel(16) + 0.7152 * channel(8) + 0.0722 * channel(0)
}

/** Applies the shared dark Material 3 color scheme and typography. */
@Composable
fun KitThemeTheme(content: @Composable () -> Unit) {
    val baseTypography = Typography()
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = KitPalette.AccentHighlight,
            onPrimary = KitPalette.Background,
            primaryContainer = KitPalette.Accent,
            onPrimaryContainer = KitPalette.OnAccent,
            secondary = KitPalette.AccentHighlight,
            onSecondary = KitPalette.OnAccent,
            secondaryContainer = KitPalette.Surface,
            onSecondaryContainer = KitPalette.Text,
            tertiary = KitPalette.AccentHighlight,
            onTertiary = KitPalette.OnAccent,
            tertiaryContainer = KitPalette.AccentPressed,
            onTertiaryContainer = KitPalette.OnAccentPressed,
            background = KitPalette.Background,
            surface = KitPalette.Surface,
            onSurface = KitPalette.Text,
            onSurfaceVariant = KitPalette.SecondaryText,
            outline = KitPalette.Border,
        ),
        typography = baseTypography.copy(
            headlineMedium = baseTypography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            titleLarge = baseTypography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        ),
        content = content,
    )
}
