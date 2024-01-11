package com.ry05k2ulv.reversiboard.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier

interface BoardState {
    val board: List<BoardCell>
}

class BoardStateImpl : BoardState {
    private var _board = mutableStateOf(List(64) { BoardCell.Empty })
    override var board: List<BoardCell>
        get() = _board.value
        set(value) {
            require(value.size == 64)
            _board.value = value
        }
}

enum class BoardCell {
    Empty,
    Black,
    White,
}

@Composable
fun Board(
    modifier: Modifier = Modifier,
    boardState: BoardState = remember { BoardStateImpl() },
) {

}