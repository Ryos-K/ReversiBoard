package com.ry05k2ulv.reversiboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ry05k2ulv.reversiboard.data.BoardInfoRepository
import com.ry05k2ulv.reversiboard.data.BoardSurfaceRepository
import com.ry05k2ulv.reversiboard.model.BoardInfo
import com.ry05k2ulv.reversiboard.reversiboard.BoardSurface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RbAppViewModel @Inject constructor(
	private val boardInfoRepository: BoardInfoRepository,
	private val boardSurfaceRepository: BoardSurfaceRepository
) : ViewModel() {
	val uiState = boardInfoRepository.getBoardInfoList().map {
		RbAppUiState.Success(it)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.Eagerly,
		initialValue = RbAppUiState.Loading
	)

	fun addBoardInfo(title: String) {
		viewModelScope.launch {
			val id = boardInfoRepository.insertBoardInfo(BoardInfo(1, title, 1, 1, emptyList(), false))
			boardSurfaceRepository.upsertBoardSurface(BoardSurface(id))
		}
	}
}

sealed interface RbAppUiState {
	object Loading : RbAppUiState
	data class Success(val boardInfoList: List<BoardInfo>) : RbAppUiState
}