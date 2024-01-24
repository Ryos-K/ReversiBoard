package com.ry05k2ulv.reversiboard.ui.home

import androidx.lifecycle.ViewModel
import com.ry05k2ulv.reversiboard.reversiboard.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
	private val reversiBoard = ReversiBoard()

	private val _uiState = MutableStateFlow(HomeUiState())
	val uiState = _uiState.asStateFlow()

	private fun syncUiState() {
		_uiState.update {
			HomeUiState(
				board = reversiBoard.boardSurface,
				lastSelectedPieceType = reversiBoard.boardSurface.expectPieceType,
				canUndo = reversiBoard.canUndo(),
				canRedo = reversiBoard.canRedo(),
			)
		}
	}

	fun updatePieceType(pieceType: PieceType) {
		_uiState.update {
			it.copy(
				lastSelectedPieceType = pieceType
			)
		}
	}

	fun dropPiece(piece: Piece): Boolean {
		val pieceIsDropped = reversiBoard.drop(piece)
		if (pieceIsDropped)
			syncUiState()
		return pieceIsDropped
	}

	fun replacePiece(piece: Piece): Boolean {
		val pieceIsReplaced = reversiBoard.replace(piece)
		if (pieceIsReplaced)
			syncUiState()
		return pieceIsReplaced
	}

	fun undo(): Boolean {
		return if (reversiBoard.canUndo()) {
			reversiBoard.undo()
			syncUiState()
			true
		} else false
	}

	fun redo(): Boolean {
		return if (reversiBoard.canRedo()) {
			reversiBoard.redo()
			syncUiState()
			true
		} else false
	}

	fun undoAll(): Boolean {
		return if (reversiBoard.canUndo()) {
			reversiBoard.undoAll()
			syncUiState()
			true
		} else false
	}

	fun redoAll(): Boolean {
		return if (reversiBoard.canRedo()) {
			reversiBoard.redoAll()
			syncUiState()
			true
		} else false
	}
}

data class HomeUiState(
	val board: BoardSurface = BoardSurface(),
	val lastSelectedPieceType: PieceType = PieceType.Black,
	val canUndo: Boolean = false,
	val canRedo: Boolean = false,
)