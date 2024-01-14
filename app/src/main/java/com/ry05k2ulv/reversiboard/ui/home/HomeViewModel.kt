package com.ry05k2ulv.reversiboard.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ry05k2ulv.reversiboard.reversiboard.BoardData
import com.ry05k2ulv.reversiboard.reversiboard.Piece
import com.ry05k2ulv.reversiboard.reversiboard.ReversiBoard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel() : ViewModel() {
    private val reversiBoard = ReversiBoard()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun dropPiece(piece: Piece, x: Int, y: Int, overwrite: Boolean, reversible: Boolean) {
        viewModelScope.launch {
            reversiBoard.drop(piece, x, y, overwrite, reversible)
            _uiState.update {
                HomeUiState(
                    board = reversiBoard.boardData,
                    canUndo = reversiBoard.canUndo(),
                    canRedo = reversiBoard.canRedo(),
                )
            }
        }
    }

    fun undo() {
        viewModelScope.launch {
            if (!reversiBoard.canUndo()) return@launch
            reversiBoard.undo()
            _uiState.value = HomeUiState(
                board = reversiBoard.boardData,
                canUndo = reversiBoard.canUndo(),
                canRedo = reversiBoard.canRedo(),
            )
        }
    }

    fun redo() {
        viewModelScope.launch {
            if (!reversiBoard.canRedo()) return@launch
            reversiBoard.redo()
            _uiState.value = HomeUiState(
                board = reversiBoard.boardData,
                canUndo = reversiBoard.canUndo(),
                canRedo = reversiBoard.canRedo(),
            )
        }
    }

    fun undoAll() {
        viewModelScope.launch {
            if (!reversiBoard.canUndo()) return@launch
            reversiBoard.undoAll()
            _uiState.value = HomeUiState(
                board = reversiBoard.boardData,
                canUndo = reversiBoard.canUndo(),
                canRedo = reversiBoard.canRedo(),
            )
        }
    }

    fun redoAll() {
        viewModelScope.launch {
            if (!reversiBoard.canRedo()) return@launch
            reversiBoard.redoAll()
            _uiState.value = HomeUiState(
                board = reversiBoard.boardData,
                canUndo = reversiBoard.canUndo(),
                canRedo = reversiBoard.canRedo(),
            )
        }
    }
}

data class HomeUiState(
    val board: BoardData = BoardData(),
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
)