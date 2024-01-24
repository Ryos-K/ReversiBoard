package com.ry05k2ulv.reversiboard.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ry05k2ulv.reversiboard.R.*
import com.ry05k2ulv.reversiboard.model.DarkThemeConfig
import com.ry05k2ulv.reversiboard.ui.theme.supportDynamicColor

@Composable
fun SettingsDialog(
	onDismiss: () -> Unit,
	viewModel: SettingsViewModel = hiltViewModel()
) {
	val settingsUiState: SettingsUiState by viewModel.settingsUiState.collectAsState()
	SettingsDialog(
		onDismiss = onDismiss,
		settingsUiState = settingsUiState,
		onChangeDynamicColor = viewModel::updateDynamicColor,
		onChangeDarkTheme = viewModel::updateDarkTheme,
	)
}

@Composable
fun SettingsDialog(
	onDismiss: () -> Unit = {},
	settingsUiState: SettingsUiState,
	supportDynamicTheme: Boolean = supportDynamicColor(),
	onChangeDynamicColor: (Boolean) -> Unit,
	onChangeDarkTheme: (DarkThemeConfig) -> Unit
) {
	AlertDialog(
		onDismissRequest = onDismiss,
		title = {
			Text(
				stringResource(string.settings_title),
				style = MaterialTheme.typography.titleLarge
			)
		},
		text = {
			Divider()
			Column(
				Modifier.verticalScroll(rememberScrollState())
			) {
				when (settingsUiState) {
					SettingsUiState.Loading -> {
						CircularProgressIndicator(Modifier.padding(20.dp))
					}

					is SettingsUiState.Success -> {
						SettingsPanel(
							settings = settingsUiState.settings,
							supportDynamicTheme = supportDynamicTheme,
							onChangeDynamicColor = onChangeDynamicColor,
							onChangeDarkTheme = onChangeDarkTheme,
						)
					}
				}
			}
		},
		confirmButton = {
			Text(
				text = stringResource(string.settings_confirm),
				style = MaterialTheme.typography.labelLarge,
				modifier = Modifier
					.padding(end = 8.dp)
					.clickable { onDismiss() }
			)
		}
	)
}

@Composable
private fun ColumnScope.SettingsPanel(
	settings: UserSettings,
	supportDynamicTheme: Boolean,
	onChangeDynamicColor: (Boolean) -> Unit,
	onChangeDarkTheme: (DarkThemeConfig) -> Unit,
) {
	if (supportDynamicTheme) {
		SectionTitle(text = stringResource(string.settings_use_dynamic_color_title))
		Column(Modifier.selectableGroup()) {
			ChooserRow(
				text = stringResource(string.settings_use_dynamic_color_yes),
				selected = settings.useDynamicColor,
				onClick = { onChangeDynamicColor(true) })
			ChooserRow(
				text = stringResource(string.settings_use_dynamic_color_no),
				selected = !settings.useDynamicColor,
				onClick = { onChangeDynamicColor(false) })
		}
	}

	SectionTitle(text = stringResource(string.settings_dark_theme_config_title))
	Column(Modifier.selectableGroup()) {
		ChooserRow(
			text = stringResource(string.settings_dark_theme_config_system),
			selected = settings.darkThemeConfig == DarkThemeConfig.SYSTEM,
			onClick = { onChangeDarkTheme(DarkThemeConfig.SYSTEM) })
		ChooserRow(
			text = stringResource(string.settings_dark_theme_config_light),
			selected = settings.darkThemeConfig == DarkThemeConfig.LIGHT,
			onClick = { onChangeDarkTheme(DarkThemeConfig.LIGHT) })
		ChooserRow(
			text = stringResource(string.settings_dark_theme_config_dark),
			selected = settings.darkThemeConfig == DarkThemeConfig.DARK,
			onClick = { onChangeDarkTheme(DarkThemeConfig.DARK) })
	}
}

@Composable
private fun SectionTitle(text: String) {
	Text(
		text = text,
		style = MaterialTheme.typography.titleMedium,
		modifier = Modifier.padding(vertical = 16.dp)
	)
}

@Composable
private fun ChooserRow(
	text: String,
	selected: Boolean,
	onClick: () -> Unit = {}
) {
	Row(
		modifier = androidx.compose.ui.Modifier
			.selectable(
				selected = selected,
				role = Role.RadioButton,
				onClick = onClick,
			)
			.fillMaxWidth()
			.padding(16.dp, 8.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		RadioButton(selected = selected, onClick = null)
		Spacer(modifier = Modifier.width(8.dp))
		Text(text = text)
	}
}