package com.ry05k2ulv.reversiboard.ui.home

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ry05k2ulv.reversiboard.R
import com.ry05k2ulv.reversiboard.reversiboard.Piece
import com.ry05k2ulv.reversiboard.reversiboard.PieceType
import com.ry05k2ulv.reversiboard.ui.components.BoardUi
import com.ry05k2ulv.reversiboard.ui.components.Mark
import com.ry05k2ulv.reversiboard.ui.components.MarkType
import com.ry05k2ulv.reversiboard.ui.home.Operation.OpErase
import com.ry05k2ulv.reversiboard.ui.home.Operation.OpMark
import com.ry05k2ulv.reversiboard.ui.home.Operation.OpNone
import com.ry05k2ulv.reversiboard.ui.home.Operation.OpPiece
import com.ry05k2ulv.reversiboard.ui.theme.ReversiBoardTheme
import kotlinx.coroutines.launch

private sealed interface Operation {
	data class OpPiece(val pieceType: PieceType) : Operation
	data class OpMark(val markType: MarkType) : Operation
	object OpErase : Operation
	object OpNone : Operation
}

@Composable
fun HomeScreen(
		viewModel: HomeViewModel = HomeViewModel(),
) {
	val context = LocalContext.current
	val scope = rememberCoroutineScope()

	val uiState by viewModel.uiState.collectAsState()

	val dropMediaPlayer = remember {
		MediaPlayer.create(context, R.raw.drop)
	}
	val replaceMediaPlayer = remember {
		MediaPlayer.create(context, R.raw.replace)
	}

	var markList by remember { mutableStateOf(emptyList<Mark>()) }

	var editMode by remember { mutableStateOf(false) }

	val operation: Operation by remember(uiState.pieceType) {
		mutableStateOf(OpPiece(uiState.pieceType))
	}

	Box(Modifier.fillMaxSize()) {
		Column(
				Modifier
						.align(Alignment.TopCenter)
		) {
			PiecePalette(
					Modifier
							.padding(16.dp)
							.fillMaxWidth()
							.height(48.dp),
					selected = uiState.pieceType,
					onPieceClick = { pieceType -> viewModel.updatePieceType(pieceType) }
			)
		}

		Column(
				Modifier
						.align(Alignment.BottomCenter)
						.fillMaxWidth()
		) {

			BoardUi(
					Modifier
							.fillMaxWidth()
							.padding(8.dp),
					uiState.board.elements,
					when (uiState.pieceType) {
						PieceType.Black -> uiState.board.blackCanDropList
						PieceType.White -> uiState.board.whiteCanDropList
						else -> emptyList()
					},
					markList
			) { x: Int, y: Int ->
				when (val operation = operation) {
					is OpErase -> {
						val lastIndex = markList.indexOfLast { it.x == x && it.y == y }
								.takeIf { it != -1 } ?: return@BoardUi
						markList = markList.toMutableList().apply { removeAt(lastIndex) }
					}

					is OpMark -> {
						if (markList.any { it.x == x && it.y == y && it.type == operation.markType }) return@BoardUi
						markList = markList.toMutableList().apply {
							add(Mark(operation.markType, x, y))
						}
					}

					is OpPiece -> {
						scope.launch {
							val pieceIsDropped = viewModel.dropPiece(Piece(uiState.pieceType, x, y))
							if (pieceIsDropped) {
								dropMediaPlayer.seekTo(0)
								dropMediaPlayer.start()
							}
						}

					}

					is OpNone -> Unit
				}
			}

			Spacer(Modifier.height(64.dp))

			Row(
					Modifier
							.fillMaxWidth()
							.padding(8.dp, 0.dp)
			) {
				UndoAllButton(
						onClick = {
							scope.launch {
								viewModel.undoAll()
								replaceMediaPlayer.seekTo(0)
								replaceMediaPlayer.start()
							}
						},
						enabled = uiState.canUndo,
						modifier = Modifier
								.weight(1f)
								.padding(4.dp)
				)
				UndoButton(
						onClick = {
							scope.launch {
								viewModel.undo()
								replaceMediaPlayer.seekTo(0)
								replaceMediaPlayer.start()
							}
						},
						enabled = uiState.canUndo,
						modifier = Modifier
								.weight(2f)
								.padding(4.dp)
				)
				RedoButton(
						onClick = {
							scope.launch {
								viewModel.redo()
								replaceMediaPlayer.seekTo(0)
								replaceMediaPlayer.start()
							}
						},
						enabled = uiState.canRedo,
						modifier = Modifier
								.weight(2f)
								.padding(4.dp)
				)
				RedoAllButton(
						onClick = {
							scope.launch {
								viewModel.redoAll()
								replaceMediaPlayer.seekTo(0)
								replaceMediaPlayer.start()
							}
						},
						enabled = uiState.canRedo,
						modifier = Modifier
								.weight(1f)
								.padding(4.dp)
				)
			}
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