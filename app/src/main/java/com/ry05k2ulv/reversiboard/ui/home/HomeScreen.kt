package com.ry05k2ulv.reversiboard.ui.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ry05k2ulv.reversiboard.reversiboard.Piece
import com.ry05k2ulv.reversiboard.reversiboard.boardWidth
import com.ry05k2ulv.reversiboard.reversiboard.isEmpty
import com.ry05k2ulv.reversiboard.ui.components.Board
import com.ry05k2ulv.reversiboard.ui.components.Mark
import com.ry05k2ulv.reversiboard.ui.components.MarkSample
import com.ry05k2ulv.reversiboard.ui.components.MarkType
import com.ry05k2ulv.reversiboard.ui.components.PieceSample
import com.ry05k2ulv.reversiboard.ui.theme.ReversiBoardTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = HomeViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var markMode by remember { mutableStateOf(false) }
    var currentPiece by remember { mutableStateOf(Piece.Black) }
    var currentMark by remember { mutableStateOf(MarkType.Circle) }

    var markList by remember { mutableStateOf(emptyList<Mark>()) }
    var eraseMarkMode by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.align(Alignment.TopCenter)
        ) {
            Text(
                text = "Piece Type",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 0.dp)
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 4.dp)
                    .height(48.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Piece.values().forEach { piece ->
                    PieceSample(
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .fillMaxHeight()
                            .aspectRatio(1.5f)
                            .clickable {
                                currentPiece = piece
                                markMode = false
                            }
                            .border(
                                if (!markMode && currentPiece == piece) 6.dp else 0.dp,
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        piece
                    )
                }
            }

            Text(
                text = "Mark Type",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 0.dp)
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 4.dp)
                    .height(48.dp)
                    .horizontalScroll(rememberScrollState()),
            ) {
                val contentModifier = Modifier
                    .padding(16.dp, 0.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxHeight()
                    .aspectRatio(1.5f)
                EraseButton(
                    onClick = {
                        markMode = true
                        eraseMarkMode = true
                    },
                    contentModifier,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        if (eraseMarkMode) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )

                MarkType.values().forEach { mark ->
                    MarkSample(
                        contentModifier
                            .clickable {
                                currentMark = mark
                                markMode = true
                                eraseMarkMode = false
                            }
                            .border(
                                if (!eraseMarkMode && markMode && currentMark == mark) 6.dp else 0.dp,
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        mark
                    )
                }
            }
        }

        Board(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(8.dp),
            uiState.board.elements,
            when (currentPiece) {
                Piece.Black -> uiState.board.blackCanDropList
                Piece.White -> uiState.board.whiteCanDropList
                else -> emptyList()
            },
            markList
        ) { x: Int, y: Int ->
            when {
                markMode && eraseMarkMode -> {
                    val lastIndex = markList.indexOfLast { it.x == x && it.y == y }
                                        .takeIf { it != -1 } ?: return@Board
                    markList = markList.toMutableList().apply { removeAt(lastIndex) }
                }

                markMode -> {
                    if (markList.any { it.x == x && it.y == y && it.type == currentMark }) return@Board
                    markList = markList.toMutableList().apply {
                        add(Mark(currentMark, x, y))
                    }
                }

                currentPiece == Piece.Black -> {
                    if (x + y * boardWidth in uiState.board.blackCanDropList)
                        viewModel.dropPiece(Piece.Black, x, y, false, true)
                }

                currentPiece == Piece.White -> {
                    if (x + y * boardWidth in uiState.board.whiteCanDropList)
                        viewModel.dropPiece(Piece.White, x, y, false, true)
                }

                currentPiece.isEmpty() -> {
                    viewModel.dropPiece(Piece.Empty, x, y, true, false)
                }
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
private fun EraseButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
    description: String = "Eraser Button",
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = colors,
    ) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = description
        )
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