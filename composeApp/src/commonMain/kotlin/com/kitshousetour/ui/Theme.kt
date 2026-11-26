package com.kitshousetour.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.kitshousetour.engine.KitMood

/** Selects one of the app's four color palettes. */
enum class ThemePalette {
    PURPLE,
    WHITE,
    BLACK,
    GREY,
}

/** Stores the selected palette and whether its dark variant is active. */
data class ThemeSettings(
    val palette: ThemePalette = ThemePalette.PURPLE,
    val isDark: Boolean = false,
)

/** The semantic colors used by one palette variant. */
data class PaletteColors(
    val background: Color,
    val surface: Color,
    val primary: Color,
    val secondary: Color,
    val onPrimary: Color,
    val text: Color,
    val mutedText: Color,
    val kitHappy: Color,
    val kitCurious: Color,
    val kitTired: Color,
    val kitProud: Color,
    val face: Color,
    val cheek: Color,
)

private val purpleLight = PaletteColors(
    background = Color(0xFFF7F2FF),
    surface = Color(0xFFFFFFFF),
    primary = Color(0xFF6750A4),
    secondary = Color(0xFFE9DDF7),
    onPrimary = Color(0xFFFFFFFF),
    text = Color(0xFF201A24),
    mutedText = Color(0xFF665D70),
    kitHappy = Color(0xFFF2B84B),
    kitCurious = Color(0xFF75B9A2),
    kitTired = Color(0xFF91A9C6),
    kitProud = Color(0xFFE88B70),
    face = Color(0xFF3D302B),
    cheek = Color(0xFFE88989),
)

private val purpleDark = PaletteColors(
    background = Color(0xFF15111D),
    surface = Color(0xFF211B2C),
    primary = Color(0xFFD0BCFF),
    secondary = Color(0xFF4F4267),
    onPrimary = Color(0xFF381E72),
    text = Color(0xFFF1ECF8),
    mutedText = Color(0xFFC9BED8),
    kitHappy = Color(0xFFFFCF68),
    kitCurious = Color(0xFF8FD4BB),
    kitTired = Color(0xFFAEC7E5),
    kitProud = Color(0xFFFFA18A),
    face = Color(0xFF352738),
    cheek = Color(0xFFFFA1A1),
)

private val whiteLight = PaletteColors(
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF7F7F7),
    primary = Color(0xFF4A4A4A),
    secondary = Color(0xFFE9E9E9),
    onPrimary = Color(0xFFFFFFFF),
    text = Color(0xFF171717),
    mutedText = Color(0xFF606060),
    kitHappy = Color(0xFFF2B84B),
    kitCurious = Color(0xFF75B9A2),
    kitTired = Color(0xFF91A9C6),
    kitProud = Color(0xFFE88B70),
    face = Color(0xFF3D302B),
    cheek = Color(0xFFE88989),
)

private val whiteDark = PaletteColors(
    background = Color(0xFF181818),
    surface = Color(0xFF242424),
    primary = Color(0xFFE6E6E6),
    secondary = Color(0xFF3A3A3A),
    onPrimary = Color(0xFF202020),
    text = Color(0xFFF4F4F4),
    mutedText = Color(0xFFC4C4C4),
    kitHappy = Color(0xFFFFCF68),
    kitCurious = Color(0xFF8FD4BB),
    kitTired = Color(0xFFAEC7E5),
    kitProud = Color(0xFFFFA18A),
    face = Color(0xFF352738),
    cheek = Color(0xFFFFA1A1),
)

private val blackLight = PaletteColors(
    background = Color(0xFFF0F0F0),
    surface = Color(0xFFFFFFFF),
    primary = Color(0xFF111111),
    secondary = Color(0xFFDADADA),
    onPrimary = Color(0xFFFFFFFF),
    text = Color(0xFF101010),
    mutedText = Color(0xFF555555),
    kitHappy = Color(0xFFE3A928),
    kitCurious = Color(0xFF4B9D82),
    kitTired = Color(0xFF718BAA),
    kitProud = Color(0xFFD26D51),
    face = Color(0xFF241E1A),
    cheek = Color(0xFFD96D72),
)

private val blackDark = PaletteColors(
    background = Color(0xFF050505),
    surface = Color(0xFF111111),
    primary = Color(0xFFE3E3E3),
    secondary = Color(0xFF292929),
    onPrimary = Color(0xFF080808),
    text = Color(0xFFF5F5F5),
    mutedText = Color(0xFFBDBDBD),
    kitHappy = Color(0xFFFFD05E),
    kitCurious = Color(0xFF83C9AA),
    kitTired = Color(0xFF9AB4D5),
    kitProud = Color(0xFFF08A6B),
    face = Color(0xFF251E1A),
    cheek = Color(0xFFF28A8E),
)

private val greyLight = PaletteColors(
    background = Color(0xFFF1F2F4),
    surface = Color(0xFFFFFFFF),
    primary = Color(0xFF59636E),
    secondary = Color(0xFFDDE1E5),
    onPrimary = Color(0xFFFFFFFF),
    text = Color(0xFF20252A),
    mutedText = Color(0xFF5C646C),
    kitHappy = Color(0xFFE8B44F),
    kitCurious = Color(0xFF69A992),
    kitTired = Color(0xFF879CB7),
    kitProud = Color(0xFFDA8068),
    face = Color(0xFF35302D),
    cheek = Color(0xFFE18484),
)

private val greyDark = PaletteColors(
    background = Color(0xFF202326),
    surface = Color(0xFF2A2E32),
    primary = Color(0xFFBAC4CE),
    secondary = Color(0xFF41474D),
    onPrimary = Color(0xFF25292D),
    text = Color(0xFFE8EAEC),
    mutedText = Color(0xFFB5BBC1),
    kitHappy = Color(0xFFF9CA65),
    kitCurious = Color(0xFF8BC8AE),
    kitTired = Color(0xFFA5BAD7),
    kitProud = Color(0xFFF2977E),
    face = Color(0xFF34302D),
    cheek = Color(0xFFF39797),
)

/** Resolves the selected palette and light/dark variant to its semantic colors. */
fun themeColors(settings: ThemeSettings): PaletteColors =
    when (settings.palette) {
        ThemePalette.PURPLE -> if (settings.isDark) purpleDark else purpleLight
        ThemePalette.WHITE -> if (settings.isDark) whiteDark else whiteLight
        ThemePalette.BLACK -> if (settings.isDark) blackDark else blackLight
        ThemePalette.GREY -> if (settings.isDark) greyDark else greyLight
    }

/** Returns settings with light and dark mode toggled, preserving the selected palette. */
fun toggleDarkMode(settings: ThemeSettings): ThemeSettings =
    settings.copy(isDark = !settings.isDark)

/** Returns the themed character color for [mood]. */
fun kitColorForMood(mood: KitMood, colors: PaletteColors): Color =
    when (mood) {
        KitMood.Happy -> colors.kitHappy
        KitMood.Curious -> colors.kitCurious
        KitMood.Tired -> colors.kitTired
        KitMood.Proud -> colors.kitProud
    }

/** Applies the selected palette and color mode across the UI. */
@Composable
fun KitThemeTheme(settings: ThemeSettings, content: @Composable () -> Unit) {
    val colors = themeColors(settings)
    val baseTypography = Typography()
    val colorScheme = if (settings.isDark) {
        darkColorScheme(
            primary = colors.primary,
            onPrimary = colors.onPrimary,
            secondary = colors.secondary,
            background = colors.background,
            surface = colors.surface,
            onSurface = colors.text,
            onSurfaceVariant = colors.mutedText,
        )
    } else {
        lightColorScheme(
            primary = colors.primary,
            onPrimary = colors.onPrimary,
            secondary = colors.secondary,
            background = colors.background,
            surface = colors.surface,
            onSurface = colors.text,
            onSurfaceVariant = colors.mutedText,
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = baseTypography.copy(
            headlineMedium = baseTypography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            titleLarge = baseTypography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        ),
        content = content,
    )
}
