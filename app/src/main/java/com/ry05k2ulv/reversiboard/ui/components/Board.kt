package com.ry05k2ulv.reversiboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ry05k2ulv.reversiboard.reversiboard.Piece
import com.ry05k2ulv.reversiboard.ui.theme.ReversiBoardTheme

//interface BoardState {
//    val markerList: List<Marker>
//    val showAssist: Boolean
//}
//
//class BoardStateImpl(
//    val width: Int = 8,
//) : BoardState {
//    private var _markerList = mutableStateOf(listOf<Marker>())
//    override var markerList: List<Marker>
//        get() = _markerList.value
//        set(value) {
//            _markerList.value = value
//        }
//
//    private var _showAssist = mutableStateOf(false)
//    override var showAssist: Boolean
//        get() = _showAssist.value
//        set(value) {
//            _showAssist.value = value
//        }
//}

enum class MarkerType {
    CanDrop,
    Circle,
    Cross,
}

data class Marker(
    val type: MarkerType,
    val x: Int,
    val y: Int,
)

@Composable
fun Board(
    modifier: Modifier = Modifier,
    pieceList: List<Piece> = emptyList(),
    markerList: List<Marker> = emptyList(),
    showAssist: Boolean = true,
    onTap: (x: Int, y: Int) -> Unit = { _, _ -> },
) {
    val filteredMarkerList =
        if (showAssist) markerList
        else markerList.filter { it.type != MarkerType.CanDrop }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            Modifier
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    detectTapGestures {
                        onTap(
                            (it.x / (size.width / 8)).toInt(),
                            (it.y / (size.height / 8)).toInt()
                        )
                    }
                }
        ) {
            drawBoard()
            drawMarker(
                pieceList,
                filteredMarkerList
            )
        }
    }
}

private fun DrawScope.drawBoard(
    background: Color = Color(0xFF0BA80B),
    background2: Color = Color(0xFF006402),
    lineColor: Color = Color.Black,
) {
    val cellWidth = size.width / 8
    val cellHeight = size.height / 8

    drawRect(
        Brush.radialGradient(
            listOf(background, background2),
            center = Offset(size.width / 2, size.height / 2)
        )
    )
    // Draw vertical lines
    repeat(9) { i ->
        drawLine(
            lineColor,
            start = Offset(cellWidth * i, 0f),
            end = Offset(cellWidth * i, size.height),
            strokeWidth = 3.dp.toPx()
        )
    }
    // Draw horizontal lines
    repeat(9) { i ->
        drawLine(
            lineColor,
            start = Offset(0f, cellHeight * i),
            end = Offset(size.width, cellHeight * i),
            strokeWidth = 3.dp.toPx()
        )
    }
    // Draw Dot
    drawCircle(
        lineColor,
        radius = 5.dp.toPx(),
        center = Offset(cellWidth * 2, cellHeight * 2),
    )
    drawCircle(
        lineColor,
        radius = 5.dp.toPx(),
        center = Offset(cellWidth * 2, cellHeight * 6),
    )
    drawCircle(
        lineColor,
        radius = 5.dp.toPx(),
        center = Offset(cellWidth * 6, cellHeight * 2),
    )
    drawCircle(
        lineColor,
        radius = 5.dp.toPx(),
        center = Offset(cellWidth * 6, cellHeight * 6),
    )
}

private fun DrawScope.drawMarker(
    pieceList: List<Piece>,
    markerList: List<Marker>,
) {
    val cellWidth = size.width / 8
    val cellHeight = size.height / 8

    pieceList.forEachIndexed { i, piece ->
        val x = i % 8
        val y = i / 8
        val center = Offset(cellWidth * x + cellWidth / 2, cellHeight * y + cellHeight / 2)
        when (piece) {
            Piece.Black -> drawBlackMarker(cellWidth * 0.4f, center)
            Piece.White -> drawWhiteMarker(cellWidth * 0.4f, center)
            else -> Unit
        }
    }
    markerList.forEach { marker ->
        val (type, x, y) = marker
        val center = Offset(cellWidth * x + cellWidth / 2, cellHeight * y + cellHeight / 2)
        when (type) {
            MarkerType.CanDrop -> drawCanDropMarker(cellWidth * 0.2f, center)
            MarkerType.Circle -> drawCircleMarker(cellWidth * 0.35f, center)
            MarkerType.Cross -> drawCrossMarker(cellWidth * 0.3f, center)
            else -> Unit
        }
    }
}


private fun DrawScope.drawBlackMarker(
    radius: Float = this.size.minDimension,
    center: Offset = this.center,
) {
    drawArc(
        color = Color.White,
        startAngle = 90f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = center - Offset(radius, radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = 2f)
    )
    drawCircle(
        Color.Black,
        radius = radius,
        center = center,
    )
}

private fun DrawScope.drawWhiteMarker(
    radius: Float = this.size.minDimension,
    center: Offset = this.center,
) {
    drawArc(
        color = Color.Black,
        startAngle = 90f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = center - Offset(radius, radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = 2f)
    )
    drawCircle(
        Color.White,
        radius = radius,
        center = center,
    )
}

private fun DrawScope.drawCanDropMarker(
    radius: Float = this.size.minDimension,
    center: Offset = this.center,
) {
    drawCircle(
        Color(0x7F000000),
        radius = radius,
        center = center,
    )
}

private fun DrawScope.drawCircleMarker(
    radius: Float = this.size.minDimension,
    center: Offset = this.center,
) {
    drawCircle(
        Color.Blue,
        radius = radius,
        center = center,
        style = Stroke(width = 5.dp.toPx())
    )
}

private fun DrawScope.drawCrossMarker(
    radius: Float = this.size.minDimension,
    center: Offset = this.center,
) {
    drawLine(
        Color.Red,
        start = center - Offset(radius, radius),
        end = center + Offset(radius, radius),
        strokeWidth = 6.dp.toPx()
    )
    drawLine(
        Color.Red,
        start = center - Offset(radius, -radius),
        end = center + Offset(radius, -radius),
        strokeWidth = 6.dp.toPx()
    )
}

@Composable
@Preview
fun BoardPreview() {
    ReversiBoardTheme {
        Surface {
            val markerList = listOf(
                Marker(MarkerType.Cross, 4, 4),
                Marker(MarkerType.CanDrop, 2, 3),
                Marker(MarkerType.CanDrop, 3, 2),
                Marker(MarkerType.CanDrop, 4, 5),
                Marker(MarkerType.CanDrop, 5, 4),
                Marker(MarkerType.Circle, 2, 1),
                Marker(MarkerType.Cross, 1, 2),
            )
            val pieceList = MutableList(64) { Piece.Empty }
                .apply {
                    this[27] = Piece.White
                    this[28] = Piece.Black
                    this[35] = Piece.Black
                    this[36] = Piece.White
                }
            Board(
                Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth(),
                pieceList,
                markerList,
                showAssist = true
            )
        }
    }
}