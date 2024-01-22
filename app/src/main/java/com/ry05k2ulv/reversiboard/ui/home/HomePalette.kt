package com.ry05k2ulv.reversiboard.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ry05k2ulv.reversiboard.reversiboard.PieceType
import com.ry05k2ulv.reversiboard.ui.components.MarkSampleUi
import com.ry05k2ulv.reversiboard.ui.components.MarkType
import com.ry05k2ulv.reversiboard.ui.components.PieceSampleUi
import com.ry05k2ulv.reversiboard.ui.theme.ReversiBoardTheme

enum class TabValue {
    Piece,
    Mark,
}

@Composable
fun rememberHomePaletteState(
        initialTabValue: TabValue = TabValue.Piece,
        initialPieceType: PieceType = PieceType.Black,
        initialMarkType: MarkType = MarkType.Circle,
): HomePaletteState {
    return rememberSaveable(saver = HomePaletteState.Saver) {
        HomePaletteState(
                initialTabValue = initialTabValue,
                initialPieceType = initialPieceType,
                initialMarkType = initialMarkType,
        )
    }
}

@Stable
class HomePaletteState(
        initialTabValue: TabValue = TabValue.Piece,
        initialPieceType: PieceType = PieceType.Black,
        initialMarkType: MarkType = MarkType.Circle,
) {
    private var _tabValue by mutableStateOf(initialTabValue)
    var tabValue: TabValue
        get() = _tabValue
        set(value) {
            _tabValue = value
        }

    private var _piece by mutableStateOf(initialPieceType)
    var pieceType: PieceType
        get() = _piece
        set(value) {
            _piece = value
        }

    private var _markType by mutableStateOf(initialMarkType)
    var markType: MarkType
        get() = _markType
        set(value) {
            _markType = value
        }

    companion object {
        val Saver = Saver<HomePaletteState, List<*>>(
            save = {
                listOf(
                        it.tabValue,
                        it.pieceType,
                        it.markType,
                )
            },
            restore = {
                HomePaletteState(
                        it[0] as TabValue,
                        it[1] as PieceType,
                        it[2] as MarkType,
                )
            }
        )
    }
}


@Composable
fun HomePalette(
    modifier: Modifier = Modifier,
    homePaletteState: HomePaletteState = rememberHomePaletteState(),
) {
    Column(modifier) {
        TabRow(selectedTabIndex = homePaletteState.tabValue.ordinal) {
            TabValue.values().forEach {
                Tab(
                    selected = it == homePaletteState.tabValue,
                    onClick = { homePaletteState.tabValue = it },
                    Modifier.padding(8.dp)
                ) {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }

        when (homePaletteState.tabValue) {
            TabValue.Piece -> PiecePalette(
                    Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(8.dp),
                    homePaletteState.pieceType
            ) { homePaletteState.pieceType = it }

            TabValue.Mark -> MarkPalette(
                    Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(8.dp),
                homePaletteState.markType
            ) { homePaletteState.markType = it }
        }
    }
}

@Composable

fun PiecePalette(
        modifier: Modifier = Modifier,
        selected: PieceType = PieceType.Black,
        onPieceClick: (PieceType) -> Unit = {},
) {
    Row(
            modifier,
            horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val shape = RoundedCornerShape(8.dp)
        PieceType.values().forEach { piece ->
            PieceSampleUi(
                    Modifier
                            .clickable { onPieceClick(piece) }
                            .clip(shape)
                            .fillMaxHeight()
                            .aspectRatio(1.5f)
                            .border(
                                    if (piece == selected) 4.dp else 0.dp,
                                    MaterialTheme.colorScheme.primary,
                                    shape
                            ),
                piece
            )
        }
    }
}

@Composable

fun MarkPalette(
        modifier: Modifier = Modifier,
        selected: MarkType = MarkType.Circle,
        onMarkClick: (MarkType) -> Unit = {},
) {
    Row(
            modifier
                    .horizontalScroll(rememberScrollState()),
    ) {
        val shape = RoundedCornerShape(8.dp)
        val contentModifier = Modifier
                .padding(8.dp, 0.dp)
                .clip(shape)
                .fillMaxHeight()
                .aspectRatio(1.5f)
        EraseButton(
            onClick = { onMarkClick(MarkType.Erase) },
            contentModifier,
            colors = IconButtonDefaults.filledIconButtonColors(
                if (MarkType.Erase == selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            ),
        )

        MarkType.values().drop(1).forEach { mark ->
            MarkSampleUi(
                    contentModifier
                            .clickable { onMarkClick(mark) }
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

//@Composable
//private fun SwitchRow(
//    checked: Boolean,
//    onCheckedChange: (Boolean) -> Unit,
//    text: String,
//    modifier: Modifier = Modifier,
//) {
//    Row(
//        modifier,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Switch(
//            checked = checked,
//            onCheckedChange = onCheckedChange,
//            modifier = Modifier.padding(8.dp)
//        )
//        Spacer(Modifier.width(16.dp))
//        Text(
//            text,
//            style = MaterialTheme.typography.titleMedium
//        )
//    }
//}

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
@Preview
fun HomePalettePreview() {
    ReversiBoardTheme {
        Surface {
            val homePaletteState = rememberHomePaletteState()
            HomePalette(Modifier, homePaletteState)
        }
    }
}