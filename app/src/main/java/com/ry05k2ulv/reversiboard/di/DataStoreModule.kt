package com.ry05k2ulv.reversiboard.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.ry05k2ulv.reversiboard.datastore.UserPreferences
import com.ry05k2ulv.reversiboard.datastore.UserPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
	@Provides
	@Singleton
	fun providesUserPreferencesDataStore(
		@ApplicationContext context: Context,
		userPreferencesSerializer: UserPreferencesSerializer
	) : DataStore<UserPreferences> =
		DataStoreFactory.create(
			serializer = userPreferencesSerializer
		) {
			context.dataStoreFile("user_preferences.pb")
		}
}