package com.ry05k2ulv.reversiboard.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.ry05k2ulv.reversiboard.data.BoardInfoRepository
import com.ry05k2ulv.reversiboard.data.BoardSurfaceRepository
import com.ry05k2ulv.reversiboard.model.BoardInfo
import com.ry05k2ulv.reversiboard.reversiboard.BoardSurface
import com.ry05k2ulv.reversiboard.reversiboard.PieceType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeArgs private constructor(
	val id: Int,
) {
	constructor(savedStateHandle: SavedStateHandle) : this(
		id = checkNotNull(savedStateHandle.get<Int>(idArg))
	)
}

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val boardInfoRepository: BoardInfoRepository,
	private val boardSurfaceRepository: BoardSurfaceRepository,
	savedStateHandle: SavedStateHandle
) : ViewModel() {
	private val id: Int

	init {
		val args = HomeArgs(savedStateHandle)
		id = args.id
		Log.d("HomeViewModel", "id: $id")
	}

	val uiState = boardInfoRepository.getBoardInfoById(id).map {
		val boardSurface = boardSurfaceRepository.getBoardSurfaceByIdAndTurn(it.id, it.currentTurn)
		if (boardSurface == null)
			HomeUiState.NotFoundBoardSurface
		else
			HomeUiState.Success(
				boardInfo = it,
				boardSurface = boardSurface,
			)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.Eagerly,
		initialValue = HomeUiState.Loading
	)

	fun updateBoardSurface(boardSurface: BoardSurface) {
		val value = uiState.value
		if (value !is HomeUiState.Success) return
		viewModelScope.launch {
			boardSurfaceRepository.upsertBoardSurface(boardSurface)
			boardInfoRepository.updateBoardInfo(
				value.boardInfo.copy(
					maxTurn = boardSurface.turn,
					currentTurn = boardSurface.turn,
				)
			)
		}
	}

	fun updatePieceType(pieceType: PieceType) {
		val value = uiState.value
		if (value !is HomeUiState.Success) return
		val next = value.boardInfo.copy(
			lastSelectedPieceType = pieceType
		)
		viewModelScope.launch {
			boardInfoRepository.updateBoardInfo(next)
		}
	}

	fun undo() {
		val value = uiState.value
		if (value !is HomeUiState.Success) return
		viewModelScope.launch {
			val next = value.boardInfo.copy(
				currentTurn = value.boardInfo.currentTurn - 1
			)
			boardInfoRepository.updateBoardInfo(next)
		}
	}

	fun redo() {
		val value = uiState.value
		if (value !is HomeUiState.Success) return
		viewModelScope.launch {
			val next = value.boardInfo.copy(
				currentTurn = value.boardInfo.currentTurn + 1
			)
			boardInfoRepository.updateBoardInfo(next)
		}
	}

	fun undoAll() {
		val value = uiState.value
		if (value !is HomeUiState.Success) return
		viewModelScope.launch {
			val next = value.boardInfo.copy(
				currentTurn = 1
			)
			boardInfoRepository.updateBoardInfo(next)
		}
	}

	fun redoAll() {
		val value = uiState.value
		if (value !is HomeUiState.Success) return
		viewModelScope.launch {
			val next = value.boardInfo.copy(
				currentTurn = value.boardInfo.maxTurn
			)
			boardInfoRepository.updateBoardInfo(next)
		}
	}
}


sealed interface HomeUiState {
	object Loading : HomeUiState

	data class Success(
		val boardSurface: BoardSurface,
		val boardInfo: BoardInfo,
	) : HomeUiState

	object NotFoundBoardInfo : HomeUiState

	object NotFoundBoardSurface : HomeUiState
}
