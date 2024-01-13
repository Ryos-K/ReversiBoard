package com.ry05k2ulv.reversiboard.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ry05k2ulv.reversiboard.reversiboard.Piece
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
    val uiState = viewModel.uiState

    var markMode by remember { mutableStateOf(false) }
    var currentPiece by remember { mutableStateOf(Piece.Black) }
    var currentMark by remember { mutableStateOf(MarkType.Circle) }

    var markList by remember { mutableStateOf(emptyList<Mark>()) }

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
                MarkType.values().forEach { mark ->
                    MarkSample(
                        Modifier
                            .padding(16.dp, 0.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .fillMaxHeight()
                            .aspectRatio(1.5f)
                            .clickable {
                                currentMark = mark
                                markMode = true
                            }
                            .border(
                                if (markMode && currentMark == mark) 6.dp else 0.dp,
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
        ) { x: Int, y: Int ->
            if (markMode) {
                markList = markList.toMutableList().apply { add(Mark(currentMark, x, y)) }
            } else {
                viewModel.dropPiece(currentPiece, x, y, false, true)
            }
        }

        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(8.dp, 0.dp)
        ) {
            UndoAllButton(
                onClick = viewModel::undo,
                enable = uiState.canUndo,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
            UndoButton(
                onClick = viewModel::undo,
                enable = uiState.canUndo,
                modifier = Modifier
                    .weight(2f)
                    .padding(4.dp)
            )
            RedoButton(
                onClick = viewModel::redo,
                enable = uiState.canRedo,
                modifier = Modifier
                    .weight(2f)
                    .padding(4.dp)
            )
            RedoAllButton(
                onClick = viewModel::redo,
                enable = uiState.canRedo,
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
    enable: Boolean = true,
    modifier: Modifier = Modifier,
    description: String = "Undo Button",
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Undo,
            contentDescription = description
        )
    }
}

@Composable
private fun RedoButton(
    onClick: () -> Unit = {},
    enable: Boolean = true,
    modifier: Modifier = Modifier,
    description: String = "Redo Button",
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
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
    enable: Boolean = true,
    modifier: Modifier = Modifier,
    description: String = "Undo Button",
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
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
    enable: Boolean = true,
    modifier: Modifier = Modifier,
    description: String = "Redo Button",
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
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