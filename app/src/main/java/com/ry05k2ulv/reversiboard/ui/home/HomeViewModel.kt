package com.ry05k2ulv.reversiboard.ui.home

import androidx.lifecycle.ViewModel
import com.ry05k2ulv.reversiboard.reversiboard.BoardSurface
import com.ry05k2ulv.reversiboard.reversiboard.Piece
import com.ry05k2ulv.reversiboard.reversiboard.PieceType
import com.ry05k2ulv.reversiboard.reversiboard.ReversiBoard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel() : ViewModel() {
	private val reversiBoard = ReversiBoard()

	private val _uiState = MutableStateFlow(HomeUiState())
	val uiState = _uiState.asStateFlow()

	fun syncUiState() {
		_uiState.update {
			HomeUiState(
					board = reversiBoard.boardSurface,
					pieceType = reversiBoard.boardSurface.expectPieceType,
					canUndo = reversiBoard.canUndo(),
					canRedo = reversiBoard.canRedo(),
			)
		}
	}

	fun updatePieceType(pieceType: PieceType) {
		_uiState.update {
			it.copy(
					pieceType = pieceType,
			)
		}
	}

	fun dropPiece(piece: Piece): Boolean {
		val pieceIsDropped = reversiBoard.drop(piece)
		if (pieceIsDropped)
			syncUiState()
		return pieceIsDropped
	}

	fun replacePiece(piece: Piece) {
		val pieceIsReplaced = reversiBoard.replace(piece)
		if (pieceIsReplaced)
			syncUiState()
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
		val pieceType: PieceType = PieceType.Black,
		val canUndo: Boolean = false,
		val canRedo: Boolean = false,
)