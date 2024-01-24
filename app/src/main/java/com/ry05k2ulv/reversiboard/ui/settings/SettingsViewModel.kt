package com.ry05k2ulv.reversiboard.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ry05k2ulv.reversiboard.data.UserDataRepository
import com.ry05k2ulv.reversiboard.model.DarkThemeConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	private val repository: UserDataRepository
) : ViewModel() {
	val settingsUiState: StateFlow<SettingsUiState> = repository.userData
		.map { userData ->
			SettingsUiState.Success(
				UserSettings(
					useDynamicColor = userData.useDynamicColor,
					darkThemeConfig = userData.darkThemeConfig
				)
			)
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.Eagerly,
			initialValue = SettingsUiState.Loading
		)

	fun updateDarkTheme(darkThemeConfig: DarkThemeConfig) {
		viewModelScope.launch {
			repository.setDarkThemeConfig(darkThemeConfig)
		}
	}

	fun updateDynamicColor(useDynamicColor: Boolean) {
		viewModelScope.launch {
			repository.setUseDynamicColor(useDynamicColor)
		}
	}
}

data class UserSettings(
	val darkThemeConfig: DarkThemeConfig,
	val useDynamicColor: Boolean
)

sealed interface SettingsUiState {
	object Loading : SettingsUiState
	data class Success(val settings: UserSettings) : SettingsUiState
}