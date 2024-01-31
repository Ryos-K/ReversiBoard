package com.ry05k2ulv.reversiboard.ui.home

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ry05k2ulv.reversiboard.R
import com.ry05k2ulv.reversiboard.reversiboard.*
import com.ry05k2ulv.reversiboard.ui.components.*
import com.ry05k2ulv.reversiboard.ui.theme.ReversiBoardTheme
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
	viewModel: HomeViewModel = hiltViewModel(),
) {
	val uiState = viewModel.uiState.collectAsState().value

	when (uiState) {
		is HomeUiState.Success -> SuccessScreen(
			Modifier.fillMaxSize(),
			uiState,
			updateBoardSurface = viewModel::updateBoardSurface,
			undo = viewModel::undo,
			redo = viewModel::redo,
			undoAll = viewModel::undoAll,
			redoAll = viewModel::redoAll,
		)

		else                   -> LoadingScreen(Modifier.fillMaxSize())
	}
}

@Composable
private fun SuccessScreen(
	modifier: Modifier,
	uiState: HomeUiState.Success,
	updateBoardSurface: (BoardSurface) -> Unit,
	undo: () -> Unit,
	redo: () -> Unit,
	undoAll: () -> Unit,
	redoAll: () -> Unit,
) {
	val context = LocalContext.current
	val scope = rememberCoroutineScope()

	val dropMediaPlayer =
		remember { MediaPlayer.create(context, R.raw.drop) }
	val replaceMediaPlayer =
		remember { MediaPlayer.create(context, R.raw.replace) }

	var markList by remember { mutableStateOf(emptyList<Mark>()) }

	var editMode by remember { mutableStateOf(false) }
	var markMode by remember { mutableStateOf(false) }

	var lastPieceType by remember(uiState.boardSurface) {
		mutableStateOf(uiState.boardSurface.expectPieceType)
	}
	var lastMarkType by remember { mutableStateOf(MarkType.Erase) }

	Column(
		modifier,
		verticalArrangement = Arrangement.SpaceBetween,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		MarkPalette(
			modifier = Modifier
				.padding(8.dp),
			selected = if (markMode) lastMarkType else null,
			onMarkChange = {
				lastMarkType = it
				markMode = true
			},
			onClearAllClick = { markList = emptyList() }
		)

		BoardUi(
			Modifier
				.fillMaxWidth()
				.padding(8.dp),
			uiState.boardSurface.elements,
			when (lastPieceType) {
				PieceType.Black -> if (!editMode) uiState.boardSurface.blackCanDropList else emptyList()
				PieceType.White -> if (!editMode) uiState.boardSurface.whiteCanDropList else emptyList()
				else            -> emptyList()
			},
			markList,
			lastPieceType
		) { x: Int, y: Int ->
			when {
				markMode && lastMarkType == MarkType.Erase -> {
					val lastIndex = markList.indexOfLast { it.x == x && it.y == y }
						.takeIf { it != -1 } ?: return@BoardUi
					markList = markList.toMutableList().apply { removeAt(lastIndex) }
				}

				markMode                                   -> {
					val index =
						markList.indexOfLast { it.x == x && it.y == y && it.type == lastMarkType }
					markList = markList.toMutableList().apply {
						if (index == -1) add(Mark(lastMarkType, x, y))
						else removeAt(index)
					}
				}

				else                                       -> {
					val piece = Piece(lastPieceType, x, y)
					Log.d("HomeScreen", "piece: $piece")
					scope.launch {
						if (editMode) {
							uiState.boardSurface.replaced(piece)?.let {
								updateBoardSurface(it)
								replaceMediaPlayer.seekTo(0)
								replaceMediaPlayer.start()
							}
						} else {
							uiState.boardSurface.dropped(piece)?.let {
								updateBoardSurface(it)
								dropMediaPlayer.seekTo(0)
								dropMediaPlayer.start()
							}
						}
					}
				}
			}
		}

		PiecePalette(
			modifier = Modifier
				.padding(16.dp)
				.fillMaxWidth(),
			selected = lastPieceType,
			onPieceClick = {
				lastPieceType = it
				markMode = false
			},
			editMode = editMode,
			onEditModeChange = {
				editMode = it
				markMode = false
			}
		)

		UndoRedoBar(
			modifier = Modifier
				.fillMaxWidth()
				.padding(8.dp, 0.dp),
			undoEnabled = uiState.boardInfo.currentTurn > 1,
			redoEnabled = uiState.boardInfo.currentTurn < uiState.boardInfo.maxTurn,
			onUndo = {
				scope.launch {
					undo()
					replaceMediaPlayer.seekTo(0)
					replaceMediaPlayer.start()
				}
			},
			onRedo = {
				scope.launch {
					redo()
					replaceMediaPlayer.seekTo(0)
					replaceMediaPlayer.start()
				}
			},
			onUndoAll = {
				scope.launch {
					undoAll()
					replaceMediaPlayer.seekTo(0)
					replaceMediaPlayer.start()
				}
			},
			onRedoAll = {
				scope.launch {
					redoAll()
					replaceMediaPlayer.seekTo(0)
					replaceMediaPlayer.start()
				}
			}
		)
	}
}

@Composable
private fun UndoRedoBar(
	modifier: Modifier,
	undoEnabled: Boolean,
	redoEnabled: Boolean,
	onUndo: () -> Unit,
	onRedo: () -> Unit,
	onUndoAll: () -> Unit,
	onRedoAll: () -> Unit,
) {

	Row(
		modifier
	) {
		UndoAllButton(
			modifier = Modifier
				.weight(1f)
				.padding(4.dp),
			onClick = onUndoAll,
			enabled = undoEnabled
		)
		UndoButton(
			modifier = Modifier
				.weight(2f)
				.padding(4.dp),
			onClick = onUndo,
			enabled = undoEnabled
		)
		RedoButton(
			modifier = Modifier
				.weight(2f)
				.padding(4.dp),
			onClick = onRedo,
			enabled = redoEnabled
		)
		RedoAllButton(
			modifier = Modifier
				.weight(1f)
				.padding(4.dp),
			onClick = onRedoAll,
			enabled = redoEnabled
		)
	}
}

@Composable
private fun UndoButton(
	modifier: Modifier,
	onClick: () -> Unit,
	enabled: Boolean = true,
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
	modifier: Modifier,
	onClick: () -> Unit,
	enabled: Boolean = true,
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
	modifier: Modifier,
	onClick: () -> Unit,
	enabled: Boolean = true,
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
	modifier: Modifier,
	onClick: () -> Unit,
	enabled: Boolean = true,
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
			HomeScreen()
		}
	}
}