package com.ry05k2ulv.reversiboard.di

import com.ry05k2ulv.reversiboard.data.BoardInfoRepository
import com.ry05k2ulv.reversiboard.data.fake.FakeBoardInfoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
	@Binds
	internal abstract fun bindsBoardInfoRepository(
		boardInfoRepository: FakeBoardInfoRepository
	): BoardInfoRepository
}