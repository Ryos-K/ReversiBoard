package com.ry05k2ulv.reversiboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import com.ry05k2ulv.reversiboard.reversiboard.BoardData
import com.ry05k2ulv.reversiboard.reversiboard.Piece
import com.ry05k2ulv.reversiboard.reversiboard.boardArea
import com.ry05k2ulv.reversiboard.reversiboard.boardWidth
import com.ry05k2ulv.reversiboard.ui.theme.ReversiBoardTheme

enum class MarkType {
    Circle,
    Cross,
}

data class Mark(
    val type: MarkType,
    val x: Int,
    val y: Int,
)

@Composable
fun Board(
    modifier: Modifier = Modifier,
    pieceList: List<Piece> = List(64) { Piece.Empty },
    canDropList: List<Int> = emptyList(),
    markList: List<Mark> = emptyList(),
    onTap: (x: Int, y: Int) -> Unit = { _, _ -> },
) {
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
            drawPieces(pieceList, canDropList)
            drawMarks(markList)
        }
    }
}

@Composable
fun PieceSample(
    modifier: Modifier = Modifier,
    piece: Piece = Piece.Empty,
) {
    Canvas(modifier) {
        val length = minOf(size.width, size.height)
        val cellLength = length * 0.9f
        // Draw background
        drawRect(
            Color(0xFF0BA80B)
        )
        // Draw vertical lines
        var x = (size.width % (cellLength * 2) - cellLength) / 2
        if (x < 0) x += cellLength
        while (x < size.width) {
            drawLine(
                Color.Black,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 3.dp.toPx()
            )
            x += cellLength
        }
        // Draw horizontal lines
        var y = (size.height % (cellLength * 2) - cellLength) / 2
        if (y < 0) y += cellLength
        while (y < size.height) {
            drawLine(
                Color.Black,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 3.dp.toPx()
            )
            y += cellLength
        }
        // Draw Piece
        when (piece) {
            Piece.Black -> drawBlackPiece(cellLength * 0.4f, center = center)
            Piece.White -> drawWhitePiece(cellLength * 0.4f, center = center)
            else -> Unit
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

private fun DrawScope.drawPieces(
    pieceList: List<Piece> = List(boardArea) { Piece.Empty },
    canDropList: List<Int> = emptyList(),
) {
    val cellWidth = size.width / boardWidth
    val cellHeight = size.height / boardWidth
    pieceList.forEachIndexed { i, piece ->
        val x = i % boardWidth
        val y = i / boardWidth
        val center = Offset(cellWidth * x + cellWidth / 2, cellHeight * y + cellHeight / 2)
        when (piece) {
            Piece.Black -> drawBlackPiece(cellWidth * 0.4f, center)
            Piece.White -> drawWhitePiece(cellWidth * 0.4f, center)
            else -> Unit
        }
    }
    canDropList.forEach { i ->
        val x = i % boardWidth
        val y = i / boardWidth
        val center = Offset(cellWidth * x + cellWidth / 2, cellHeight * y + cellHeight / 2)
        drawCanDropDot(cellWidth * 0.2f, center)
    }
}

private fun DrawScope.drawMarks(
    markList: List<Mark>,
) {
    val cellWidth = size.width / boardWidth
    val cellHeight = size.height / boardWidth

    markList.forEach { mark ->
        val (type, x, y) = mark
        val center = Offset(cellWidth * x + cellWidth / 2, cellHeight * y + cellHeight / 2)
        when (type) {
            MarkType.Circle -> drawCircleMark(cellWidth * 0.35f, center)
            MarkType.Cross -> drawCrossMark(cellWidth * 0.3f, center)
        }
    }
}


private fun DrawScope.drawBlackPiece(
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

private fun DrawScope.drawWhitePiece(
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

private fun DrawScope.drawCanDropDot(
    radius: Float = this.size.minDimension,
    center: Offset = this.center,
) {
    drawCircle(
        Color(0x7F000000),
        radius = radius,
        center = center,
    )
}

private fun DrawScope.drawCircleMark(
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

private fun DrawScope.drawCrossMark(
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
            val markList = listOf(
                Mark(MarkType.Cross, 4, 4),
                Mark(MarkType.Circle, 2, 1),
                Mark(MarkType.Cross, 1, 2),
            )
            val boardData = BoardData(
                MutableList(64) { Piece.Empty }
                    .apply {
                        this[27] = Piece.White
                        this[28] = Piece.Black
                        this[35] = Piece.Black
                        this[36] = Piece.White
                        this[37] = Piece.Black
                    }
            )
            Board(
                Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth(),
                boardData.elements,
                boardData.blackCanDropList,
                markList,
            )
        }
    }
}

@Composable
@Preview
private fun PieceSamplePreview() {
    ReversiBoardTheme {
        Surface {
            PieceSample(
                Modifier
                    .aspectRatio(1.5f)
                    .fillMaxWidth(),
                Piece.Black
            )
        }
    }
}