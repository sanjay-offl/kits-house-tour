package com.kitshousetour.ui

import androidx.compose.ui.graphics.toArgb
import kotlin.test.Test
import kotlin.test.assertTrue

class KitPaletteTest {
    @Test
    fun allUsedTextAndAccentPairsMeetWcagAaContrast() {
        val requiredContrast = 4.5
        val checkedRatios = listOf(
            KitPaletteContrast.TextOnBackground,
            KitPaletteContrast.TextOnSurface,
            KitPaletteContrast.SecondaryTextOnBackground,
            KitPaletteContrast.SecondaryTextOnSurface,
            KitPaletteContrast.AccentOnBackground,
            KitPaletteContrast.HighlightOnBackground,
            KitPaletteContrast.HighlightOnSurface,
            KitPaletteContrast.OnAccentOnAccent,
            KitPaletteContrast.OnBackgroundOnHighlight,
            KitPaletteContrast.OnPressedAccentOnPressedAccent,
        )

        checkedRatios.forEach { ratio ->
            assertTrue(
                ratio >= requiredContrast,
                "Expected contrast ratio >= $requiredContrast, got $ratio",
            )
        }
    }

    @Test
    fun paletteColorsUseTheDeclaredHexValues() {
        assertTrue(KitPalette.Background.toArgb() == 0xFF0B0B0F.toInt())
        assertTrue(KitPalette.Surface.toArgb() == 0xFF16161D.toInt())
        assertTrue(KitPalette.Accent.toArgb() == 0xFF8B5CF6.toInt())
        assertTrue(KitPalette.AccentHighlight.toArgb() == 0xFFC4B5FD.toInt())
        assertTrue(KitPalette.AccentPressed.toArgb() == 0xFF5B21B6.toInt())
    }
}
