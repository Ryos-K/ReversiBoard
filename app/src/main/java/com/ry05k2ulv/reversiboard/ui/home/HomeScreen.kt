package com.ry05k2ulv.reversiboard.ui.home

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ry05k2ulv.reversiboard.R
import com.ry05k2ulv.reversiboard.reversiboard.Piece
import com.ry05k2ulv.reversiboard.reversiboard.PieceType
import com.ry05k2ulv.reversiboard.ui.components.*
import com.ry05k2ulv.reversiboard.ui.theme.ReversiBoardTheme
import kotlinx.coroutines.launch


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
	var markMode by remember { mutableStateOf(false) }

	var lastMarkType by remember { mutableStateOf(MarkType.Erase) }

	var offsetX by remember { mutableStateOf(0) }
	var offsetY by remember { mutableStateOf(0) }

	Box(Modifier.fillMaxSize()) {

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
				when (uiState.lastSelectedPieceType) {
					PieceType.Black -> if (!editMode) uiState.board.blackCanDropList else emptyList()
					PieceType.White -> if (!editMode) uiState.board.whiteCanDropList else emptyList()
					else            -> emptyList()
				},
				markList
			) { x: Int, y: Int ->
				when {
					markMode && lastMarkType == MarkType.Erase -> {
						val lastIndex = markList.indexOfLast { it.x == x && it.y == y }
							.takeIf { it != -1 } ?: return@BoardUi
						markList = markList.toMutableList().apply { removeAt(lastIndex) }
					}

					markMode                                   -> {
						if (markList.any { it.x == x && it.y == y && it.type == lastMarkType }) return@BoardUi
						markList = markList.toMutableList().apply {
							add(Mark(lastMarkType, x, y))
						}
					}

					else                                       -> {
						val piece = Piece(uiState.lastSelectedPieceType, x, y)
						Log.d("HomeScreen", "dropPiece: $piece")
						scope.launch {
							val success = when (editMode) {
								true -> viewModel.replacePiece(piece)
								false -> viewModel.dropPiece(piece)
							}
							if (success) {
								dropMediaPlayer.seekTo(0)
								dropMediaPlayer.start()
							}
						}
					}
				}
			}

			Spacer(Modifier.height(16.dp))

			PiecePalette(
				modifier = Modifier
					.padding(16.dp)
					.fillMaxWidth(),
				selected = uiState.lastSelectedPieceType,
				onPieceClick = {
					viewModel.updatePieceType(it)
					markMode = false
				},
				editMode = editMode,
				onEditModeChange = {
					editMode = it
					markMode = false
				}
			)

			Spacer(Modifier.height(16.dp))

			UndoRedoBar(
				modifier = Modifier
					.fillMaxWidth()
					.padding(8.dp, 0.dp),
				undoEnabled = uiState.canUndo,
				redoEnabled = uiState.canRedo,
				onUndo = {
					scope.launch {
						viewModel.undo()
						replaceMediaPlayer.seekTo(0)
						replaceMediaPlayer.start()
					}
				},
				onRedo = {
					scope.launch {
						viewModel.redo()
						replaceMediaPlayer.seekTo(0)
						replaceMediaPlayer.start()
					}
				},
				onUndoAll = {
					scope.launch {
						viewModel.undoAll()
						replaceMediaPlayer.seekTo(0)
						replaceMediaPlayer.start()
					}
				},
				onRedoAll = {
					scope.launch {
						viewModel.redoAll()
						replaceMediaPlayer.seekTo(0)
						replaceMediaPlayer.start()
					}
				}
			)

		}
		MarkPalette(
			modifier = Modifier
				.align(Alignment.TopStart)
				.padding(8.dp)
				.offset { IntOffset(offsetX, offsetY) }
				.pointerInput(Unit) {
					detectDragGestures { change: PointerInputChange, dragAmount: Offset ->
						change.consume()
						offsetX += dragAmount.x.toInt()
						offsetY += dragAmount.y.toInt()
					}
				},
			selected = lastMarkType,
			onMarkChange = { lastMarkType = it },
			expanded = markMode,
			onExpandedChange = { markMode = it },
			onClearAllClick = { markList = emptyList() }
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
			val viewModel = HomeViewModel()
			HomeScreen(viewModel)
		}
	}
}