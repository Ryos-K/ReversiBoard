package com.ry05k2ulv.reversiboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ry05k2ulv.reversiboard.data.fake.FakeBoardInfoRepository
import com.ry05k2ulv.reversiboard.model.BoardInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class RbAppViewModel @Inject constructor(
	private val boardInfoRepository: FakeBoardInfoRepository
): ViewModel() {
	val uiState = boardInfoRepository.getBoardInfoList().map {
		RbAppUiState.Success(it)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.Eagerly,
		initialValue = RbAppUiState.Loading
	)
}

sealed interface RbAppUiState {
	object Loading: RbAppUiState
	data class Success(val boardInfoList: List<BoardInfo>): RbAppUiState
}