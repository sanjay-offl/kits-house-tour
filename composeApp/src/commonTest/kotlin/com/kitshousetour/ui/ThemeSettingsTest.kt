package com.kitshousetour.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ThemeSettingsTest {
    @Test
    fun everyPaletteHasDistinctLightAndDarkColors() {
        ThemePalette.entries.forEach { palette ->
            val light = themeColors(ThemeSettings(palette = palette, isDark = false))
            val dark = themeColors(ThemeSettings(palette = palette, isDark = true))

            assertNotEquals(light.background, dark.background, "$palette background")
            assertNotEquals(light.surface, dark.surface, "$palette surface")
            assertNotEquals(light.primary, dark.primary, "$palette primary")
            assertNotEquals(light.secondary, dark.secondary, "$palette secondary")
            assertNotEquals(light.text, dark.text, "$palette text")
            assertNotEquals(light.mutedText, dark.mutedText, "$palette muted text")
            assertNotEquals(light.onPrimary, dark.onPrimary, "$palette on-primary")
        }
    }

    @Test
    fun palettesHaveUniqueColorSetsInBothModes() {
        listOf(false, true).forEach { isDark ->
            val colors = ThemePalette.entries.map { palette ->
                themeColors(ThemeSettings(palette = palette, isDark = isDark))
            }

            assertEquals(colors.size, colors.toSet().size)
        }
    }

    @Test
    fun togglingDarkModePreservesTheSelectedPalette() {
        val settings = ThemeSettings(palette = ThemePalette.GREY, isDark = false)

        val darkSettings = toggleDarkMode(settings)

        assertEquals(ThemePalette.GREY, darkSettings.palette)
        assertEquals(true, darkSettings.isDark)
        assertEquals(settings, toggleDarkMode(darkSettings))
    }
}
