package com.ry05k2ulv.reversiboard.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ry05k2ulv.reversiboard.reversiboard.PieceType
import com.ry05k2ulv.reversiboard.ui.components.*
import com.ry05k2ulv.reversiboard.ui.theme.ReversiBoardTheme

sealed interface Operation {
	data class OpPiece(val pieceType: PieceType) : Operation
	data class OpMark(val markType: MarkType) : Operation
	object OpErase : Operation
	object OpNone : Operation
}

@Composable
fun MarkPalette(
	modifier: Modifier,
	selected: MarkType,
	onMarkChange: (MarkType) -> Unit,
	expanded: Boolean,
	onExpandedChange: (Boolean) -> Unit,
) {
	Card(modifier.animateContentSize()) {
		PaletteToggleButton(
			checked = expanded,
			onCheckedChange = onExpandedChange,
			modifier = Modifier
				.padding(4.dp)
				.size(40.dp)
		)
		if (expanded) {
			Row(
				Modifier.horizontalScroll(rememberScrollState()),
			) {
				val shape = RoundedCornerShape(8.dp)
				val contentModifier = Modifier
					.padding(8.dp, 0.dp)
					.height(48.dp)
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
							), mark)
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
	Card(modifier) {
		SwitchRow(
			checked = editMode,
			onCheckedChange = onEditModeChange,
			text = "Edit Mode",
			modifier = Modifier.padding(16.dp, 0.dp)
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
private fun SwitchRow(
	checked: Boolean,
	onCheckedChange: (Boolean) -> Unit,
	text: String,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier,
		verticalAlignment = Alignment.CenterVertically
	) {
		Switch(
			checked = checked,
			onCheckedChange = onCheckedChange,
			modifier = Modifier.padding(8.dp)
		)
		Spacer(Modifier.width(16.dp))
		Text(
			text,
			style = MaterialTheme.typography.titleMedium
		)
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
fun HomePalettePreview() {
	ReversiBoardTheme {
		Surface {
		}
	}
}