package com.ry05k2ulv.reversiboard.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ry05k2ulv.reversiboard.reversiboard.Piece
import com.ry05k2ulv.reversiboard.reversiboard.ReversiBoard
import com.ry05k2ulv.reversiboard.ui.components.BoardState
import com.ry05k2ulv.reversiboard.ui.components.BoardStateImpl

class HomeViewModel() : ViewModel() {
    private val reversiBoard = ReversiBoard()

    private var _uiState = mutableStateOf(HomeUiState())
    val uiState: HomeUiState
        get() = _uiState.value
}

data class HomeUiState(
    val elements: List<Piece> = emptyList(),
    val enableFlip: Boolean = true,
    val enableOverwrite: Boolean = false,
    val changeTurn: Boolean = true,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
)