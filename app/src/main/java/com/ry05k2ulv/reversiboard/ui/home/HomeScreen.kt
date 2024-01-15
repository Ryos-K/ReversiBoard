package com.ry05k2ulv.reversiboard.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ry05k2ulv.reversiboard.reversiboard.Piece
import com.ry05k2ulv.reversiboard.reversiboard.boardWidth
import com.ry05k2ulv.reversiboard.reversiboard.isEmpty
import com.ry05k2ulv.reversiboard.ui.components.Board
import com.ry05k2ulv.reversiboard.ui.components.Mark
import com.ry05k2ulv.reversiboard.ui.components.MarkType
import com.ry05k2ulv.reversiboard.ui.home.Operation.*
import com.ry05k2ulv.reversiboard.ui.theme.ReversiBoardTheme

private sealed interface Operation {
    data class OpPiece(val piece: Piece, val overwrite: Boolean, val reversible: Boolean) :
        Operation

    data class OpMark(val markType: MarkType) : Operation
    object OpErase : Operation
    object OpNone : Operation
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = HomeViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val homePaletteState = rememberHomePaletteState()

    var markList by remember { mutableStateOf(emptyList<Mark>()) }

    val operation: Operation by remember(
        homePaletteState.tabValue,
        homePaletteState.piece,
        homePaletteState.markType
    ) {
        derivedStateOf {
            when (homePaletteState.tabValue) {
                TabValue.Piece    -> OpPiece(homePaletteState.piece, false, true)
                TabValue.Mark     ->
                    if (homePaletteState.markType == MarkType.Erase) OpErase
                    else OpMark(homePaletteState.markType)
                TabValue.Settings -> OpNone
            }
        }
    }


    Box(Modifier.fillMaxSize()) {

        HomePalette(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            homePaletteState
        )

        Board(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(8.dp),
            uiState.board.elements,
            when (homePaletteState.piece) {
                Piece.Black -> uiState.board.blackCanDropList
                Piece.White -> uiState.board.whiteCanDropList
                else        -> emptyList()
            },
            markList
        ) { x: Int, y: Int ->
            when (val operation = operation) {
                is OpErase -> {
                    val lastIndex = markList.indexOfLast { it.x == x && it.y == y }
                                        .takeIf { it != -1 } ?: return@Board
                    markList = markList.toMutableList().apply { removeAt(lastIndex) }
                }
                is OpMark  -> {
                    if (markList.any { it.x == x && it.y == y && it.type == operation.markType }) return@Board
                    markList = markList.toMutableList().apply {
                        add(Mark(operation.markType, x, y))
                    }
                }
                is OpPiece -> viewModel.dropPiece(
                    operation.piece,
                    x,
                    y,
                    operation.overwrite,
                    operation.reversible
                )
                is OpNone  -> Unit
            }
        }

        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(8.dp, 0.dp)
        ) {
            UndoAllButton(
                onClick = viewModel::undoAll,
                enabled = uiState.canUndo,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
            UndoButton(
                onClick = viewModel::undo,
                enabled = uiState.canUndo,
                modifier = Modifier
                    .weight(2f)
                    .padding(4.dp)
            )
            RedoButton(
                onClick = viewModel::redo,
                enabled = uiState.canRedo,
                modifier = Modifier
                    .weight(2f)
                    .padding(4.dp)
            )
            RedoAllButton(
                onClick = viewModel::redoAll,
                enabled = uiState.canRedo,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
        }
    }
}

@Composable
private fun UndoButton(
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    description: String = "Undo Button",
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Undo,
            contentDescription = description,
            modifier = Modifier.fillMaxHeight()
        )
    }
}

@Composable
private fun RedoButton(
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    description: String = "Redo Button",
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Redo,
            contentDescription = description
        )
    }
}

@Composable
private fun UndoAllButton(
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    description: String = "Undo Button",
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.KeyboardDoubleArrowLeft,
            contentDescription = description
        )
    }
}

@Composable
private fun RedoAllButton(
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    description: String = "Redo Button",
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.KeyboardDoubleArrowRight,
            contentDescription = description
        )
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    ReversiBoardTheme {
        Surface {
            val viewModel = HomeViewModel()
            HomeScreen(viewModel)
        }
    }
}