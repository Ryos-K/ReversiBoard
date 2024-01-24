package com.ry05k2ulv.reversiboard.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomColorScheme(
	val boardBackground1: Color = Color.Unspecified,
	val boardBackground2: Color = Color.Unspecified,
)

internal val LocalCustomColorScheme  = staticCompositionLocalOf { CustomColorScheme() }
