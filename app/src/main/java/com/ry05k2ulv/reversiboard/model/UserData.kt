package com.ry05k2ulv.reversiboard.model

data class UserData(
	val useDynamicColor: Boolean,
	val darkThemeConfig: DarkThemeConfig
)

enum class DarkThemeConfig {
	SYSTEM, LIGHT, DARK
}