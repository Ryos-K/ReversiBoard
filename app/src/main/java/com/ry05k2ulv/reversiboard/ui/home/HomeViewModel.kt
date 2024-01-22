package com.ry05k2ulv.reversiboard.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ry05k2ulv.reversiboard.reversiboard.BoardSurface
import com.ry05k2ulv.reversiboard.reversiboard.Piece
import com.ry05k2ulv.reversiboard.reversiboard.PieceType
import com.ry05k2ulv.reversiboard.reversiboard.ReversiBoard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    fun undo() {
        viewModelScope.launch {
            if (!reversiBoard.canUndo()) return@launch
            reversiBoard.undo()
            syncUiState()
        }
    }

    fun redo() {
        viewModelScope.launch {
            if (!reversiBoard.canRedo()) return@launch
            reversiBoard.redo()
            syncUiState()
        }
    }

    fun undoAll() {
        viewModelScope.launch {
            if (!reversiBoard.canUndo()) return@launch
            reversiBoard.undoAll()
            syncUiState()
        }
    }

    fun redoAll() {
        viewModelScope.launch {
            if (!reversiBoard.canRedo()) return@launch
            reversiBoard.redoAll()
            syncUiState()
        }
    }
}

data class HomeUiState(
        val board: BoardSurface = BoardSurface(),
        val pieceType: PieceType = PieceType.Black,
        val canUndo: Boolean = false,
        val canRedo: Boolean = false,
)