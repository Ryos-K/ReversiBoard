package com.ry05k2ulv.reversiboard.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ry05k2ulv.reversiboard.reversiboard.PieceType
import com.ry05k2ulv.reversiboard.ui.components.*
import com.ry05k2ulv.reversiboard.ui.theme.ReversiBoardTheme

@Stable
class FloatingPaletteState()

@Composable
fun MarkPalette(
	modifier: Modifier,
	selected: MarkType,
	onMarkChange: (MarkType) -> Unit,
	expanded: Boolean,
	onExpandedChange: (Boolean) -> Unit,
	onClearAllClick: () -> Unit,
) {
	Card(modifier.animateContentSize()) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			PaletteToggleButton(
				checked = expanded,
				onCheckedChange = onExpandedChange,
				modifier = Modifier
					.padding(4.dp)
					.size(40.dp)
			)
			if (expanded) {
				TextButton(onClick = onClearAllClick) {
					Text("Clear All", style = MaterialTheme.typography.titleMedium)
				}
			}
		}
		if (expanded) {
			Row(
				Modifier
					.fillMaxWidth(0.8f)
					.horizontalScroll(rememberScrollState()),
			) {
				val shape = RoundedCornerShape(8.dp)
				val contentModifier = Modifier
					.padding(8.dp)
					.height(40.dp)
					.clip(shape)
					.fillMaxHeight()
					.aspectRatio(1.3f)
				EraseButton(
					onClick = { onMarkChange(MarkType.Erase) },
					contentModifier,
					colors = IconButtonDefaults.filledIconButtonColors(
						if (MarkType.Erase == selected) MaterialTheme.colorScheme.primary
						else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
					),
				)

				MarkType.values().drop(1).forEach { mark ->
					MarkSampleUi(
						contentModifier
							.clickable { onMarkChange(mark) }
							.border(
								if (mark == selected) 4.dp else 0.dp,
								MaterialTheme.colorScheme.primary,
								shape
							),
						mark
					)
				}
			}
		}
	}
}

@Composable
fun PiecePalette(
	modifier: Modifier,
	selected: PieceType,
	onPieceClick: (PieceType) -> Unit,
	editMode: Boolean,
	onEditModeChange: (Boolean) -> Unit,
) {
	Row(
		modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(8.dp))
			.background(MaterialTheme.colorScheme.surfaceVariant),
		verticalAlignment = Alignment.CenterVertically
	) {
		EditToggleButton(
			checked = editMode,
			onCheckedChange = onEditModeChange,
			modifier = Modifier
				.padding(4.dp)
		)

		Row(
			Modifier
				.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically
		) {
			val shape = RoundedCornerShape(8.dp)
			if (editMode) {
				PieceType.values().forEach { pieceType ->
					PieceSampleUi(
						Modifier
							.padding(16.dp, 4.dp)
							.clickable { onPieceClick(pieceType) }
							.clip(shape)
							.height(48.dp)
							.aspectRatio(1.3f)
							.border(
								if (pieceType == selected) 4.dp else 0.dp,
								MaterialTheme.colorScheme.primary,
								shape
							),
						pieceType
					)
				}
			} else {
				PieceSampleUi(
					Modifier
						.padding(16.dp, 4.dp)
						.clickable { onPieceClick(selected) }
						.clip(shape)
						.height(48.dp)
						.fillMaxHeight()
						.aspectRatio(1.3f),
					pieceType = selected
				)
			}
		}
	}
}

@Composable
private fun PaletteToggleButton(
	checked: Boolean,
	onCheckedChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {
	FilledIconButton(
		onClick = { onCheckedChange(!checked) },
		modifier = modifier,
		shape = RoundedCornerShape(8.dp),
		colors = IconButtonDefaults.iconButtonColors(
			if (checked) MaterialTheme.colorScheme.primary
			else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
		),
	) {
		Icon(
			imageVector = Icons.Default.Palette,
			contentDescription = "Palette Toggle Button",
			modifier = Modifier.size(32.dp)
		)
	}
}

@Composable
private fun EditToggleButton(
	checked: Boolean,
	onCheckedChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {
	IconToggleButton(
		checked = checked,
		onCheckedChange = onCheckedChange,
		modifier = modifier,
	) {
		Icon(
			imageVector = Icons.Default.Edit,
			contentDescription = "Palette Toggle Button",
			modifier = Modifier
				.padding(8.dp)
				.size(32.dp)
		)
	}
}

@Composable
private fun EraseButton(
	onClick: () -> Unit,
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
		Icon(imageVector = Icons.Default.Clear, contentDescription = description)
	}
}

@Composable
@Preview
fun PiecePalettePreview() {
	ReversiBoardTheme {
		Surface {
			PiecePalette(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				selected = PieceType.Black,
				onPieceClick = {},
				editMode = true,
				onEditModeChange = {},
			)
		}
	}
}

@Composable
@Preview
fun MarkPalettePreview() {
	ReversiBoardTheme {
		Surface {
			MarkPalette(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				selected = MarkType.Circle,
				onMarkChange = {},
				expanded = true,
				onExpandedChange = {},
				onClearAllClick = {},
			)
		}
	}
}