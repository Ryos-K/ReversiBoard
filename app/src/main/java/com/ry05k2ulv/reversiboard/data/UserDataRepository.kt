package com.ry05k2ulv.reversiboard.data

import com.ry05k2ulv.reversiboard.datastore.RbPreferencesDataSource
import com.ry05k2ulv.reversiboard.model.DarkThemeConfig
import com.ry05k2ulv.reversiboard.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDataRepository @Inject constructor(
	private val preferenceDataSource: RbPreferencesDataSource
) {
	val userData: Flow<UserData> = preferenceDataSource.userData

	suspend fun setUseDynamicColor(useDynamicColor: Boolean) {
		preferenceDataSource.setUseDynamicColor(useDynamicColor)
	}

	suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
		preferenceDataSource.setDarkThemeConfig(darkThemeConfig)
	}
}