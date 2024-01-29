package com.ry05k2ulv.reversiboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ry05k2ulv.reversiboard.reversiboard.*
import com.ry05k2ulv.reversiboard.ui.theme.LocalCustomColorScheme
import com.ry05k2ulv.reversiboard.ui.theme.ReversiBoardTheme

enum class MarkType {
	Erase,
	Circle,
	Cross,
	Triangle,
	Question,
	Exclamation,
}

data class Mark(
	val type: MarkType,
	val x: Int,
	val y: Int,
)

@Composable
fun BoardUi(
	modifier: Modifier = Modifier,
	pieceTypeList: List<PieceType> = List(64) { PieceType.Empty },
	canDropList: List<Int> = emptyList(),
	markList: List<Mark> = emptyList(),
	pieceType: PieceType,
	onTap: (x: Int, y: Int) -> Unit = { _, _ -> },
) {
	val textMeasurer = rememberTextMeasurer()

	val background1 = LocalCustomColorScheme.current.boardBackground1
	val background2 = LocalCustomColorScheme.current.boardBackground2

	Box(
		modifier = modifier,
		contentAlignment = Alignment.Center
	) {
		Canvas(
			Modifier
				.aspectRatio(1f)
				.pointerInput(pieceType) {
					detectTapGestures {
						onTap(
							(it.x / (size.width / boardWidth)).toInt(),
							(it.y / (size.height / boardWidth)).toInt()
						)
					}
				}
		) {
			drawBoard(
				background1, background2
			)
			drawPieces(pieceTypeList, canDropList)
			drawMarks(markList, textMeasurer)
		}
	}
}

@Composable
fun PieceSampleUi(
	modifier: Modifier = Modifier,
	pieceType: PieceType = PieceType.Empty,
) {
	val background = LocalCustomColorScheme.current.boardBackground1

	Canvas(modifier) {
		val length = minOf(size.width, size.height)
		val cellLength = length * 0.8f

		// Draw background
		drawSampleBoard(background)
		// Draw Piece
		when (pieceType) {
			PieceType.Black -> drawBlackPiece(cellLength * 0.4f, center = center)
			PieceType.White -> drawWhitePiece(cellLength * 0.4f, center = center)
			else            -> Unit
		}
	}
}

@Composable
fun MarkSampleUi(
	modifier: Modifier = Modifier,
	markType: MarkType = MarkType.Circle,
) {
	val textMeasurer = rememberTextMeasurer()
	val background = LocalCustomColorScheme.current.boardBackground1

	Canvas(modifier) {
		val length = minOf(size.width, size.height)
		val cellLength = length * 0.8f

		// Draw background
		drawSampleBoard(background)
		// Draw Mark
		when (markType) {
			MarkType.Circle      -> drawCircleMark(cellLength * 0.35f, center)
			MarkType.Cross       -> drawCrossMark(cellLength * 0.3f, center)
			MarkType.Triangle    -> drawTriangleMark(cellLength * 0.35f, center)
			MarkType.Question    -> drawQuestionMark(textMeasurer, cellLength * 0.4f, center)
			MarkType.Exclamation -> drawExclamationMark(textMeasurer, cellLength * 0.4f, center)
			else                 -> Unit
		}
	}
}

fun DrawScope.drawSampleBoard(
	background: Color = Color(0xFF0BA80B),
) {
	val length = minOf(size.width, size.height)
	val cellLength = length * 0.8f
	// Draw background
	drawRect(background)
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
}

private fun DrawScope.drawBoard(
	background1: Color = Color(0xFF0BA80B),
	background2: Color = Color(0xFF006402),
	lineColor: Color = Color.Black,
) {
	val cellWidth = size.width / 8
	val cellHeight = size.height / 8

	drawRect(
		Brush.radialGradient(
			listOf(background1, background2),
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
	pieceTypeList: List<PieceType> = List(boardArea) { PieceType.Empty },
	canDropList: List<Int> = emptyList(),
) {
	val cellWidth = size.width / boardWidth
	val cellHeight = size.height / boardWidth
	pieceTypeList.forEachIndexed { i, piece ->
		val x = i % boardWidth
		val y = i / boardWidth
		val center = Offset(cellWidth * x + cellWidth / 2, cellHeight * y + cellHeight / 2)
		when (piece) {
			PieceType.Black -> drawBlackPiece(cellWidth * 0.4f, center)
			PieceType.White -> drawWhitePiece(cellWidth * 0.4f, center)
			else            -> Unit
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
	textMeasurer: TextMeasurer,
) {
	val cellWidth = size.width / boardWidth
	val cellHeight = size.height / boardWidth

	markList.forEach { mark ->
		val (type, x, y) = mark
		val center = Offset(cellWidth * x + cellWidth / 2, cellHeight * y + cellHeight / 2)
		when (type) {
			MarkType.Circle      -> drawCircleMark(cellWidth * 0.35f, center)
			MarkType.Cross       -> drawCrossMark(cellWidth * 0.3f, center)
			MarkType.Triangle    -> drawTriangleMark(cellWidth * 0.35f, center)
			MarkType.Question    -> drawQuestionMark(textMeasurer, cellWidth * 0.4f, center)
			MarkType.Exclamation -> drawExclamationMark(textMeasurer, cellWidth * 0.4f, center)
			else                 -> Unit
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

private fun DrawScope.drawTriangleMark(
	radius: Float = this.size.minDimension,
	center: Offset = this.center,
) {
	val topVertex = center + Offset(0f, -radius * 0.866f)
	val leftVertex = center + Offset(-radius, radius * 0.866f)
	val rightVertex = center + Offset(radius, radius * 0.866f)
	drawPath(
		Path().apply {
			moveTo(topVertex.x, topVertex.y)
			lineTo(leftVertex.x, leftVertex.y)
			lineTo(rightVertex.x, rightVertex.y)
			close()
		},
		color = Color.Green,
		style = Stroke(5.dp.toPx(), pathEffect = PathEffect.cornerPathEffect(4.dp.toPx()))
	)
}

private fun DrawScope.drawQuestionMark(
	textMeasurer: TextMeasurer,
	radius: Float = this.size.minDimension,
	center: Offset = this.center,
) {
	drawText(
		textMeasurer,
		"?",
		center - Offset(radius, radius),
		style = TextStyle(fontSize = (radius * 2).toSp(), color = Color.Magenta)
	)
}

private fun DrawScope.drawExclamationMark(
	textMeasurer: TextMeasurer,
	radius: Float = this.size.minDimension,
	center: Offset = this.center,
) {
	drawText(
		textMeasurer,
		"!",
		center - Offset(radius, radius),
		style = TextStyle(fontSize = (radius * 2).toSp(), color = Color.Magenta)
	)
}

//@Composable
//@Preview
//fun BoardPreview() {
//	ReversiBoardTheme {
//		Surface {
//			val markList = listOf(
//				Mark(MarkType.Cross, 4, 4),
//				Mark(MarkType.Circle, 2, 1),
//				Mark(MarkType.Cross, 1, 2),
//				Mark(MarkType.Triangle, 6, 6),
//				Mark(MarkType.Question, 5, 6),
//				Mark(MarkType.Exclamation, 4, 6),
//			)
//			val boardSurface = BoardSurface(
//				elements = MutableList(64) { PieceType.Empty }
//					.apply {
//						this[27] = PieceType.White
//						this[28] = PieceType.Black
//						this[35] = PieceType.Black
//						this[36] = PieceType.White
//						this[37] = PieceType.Black
//					}
//			)
//			BoardUi(
//				Modifier
//					.aspectRatio(1f)
//					.fillMaxWidth(),
//				boardSurface.elements,
//				boardSurface.blackCanDropList,
//				markList,
//
//			)
//		}
//	}
//}

@Composable
@Preview
private fun PieceSamplePreview() {
	ReversiBoardTheme {
		Surface {
			PieceSampleUi(
				Modifier
					.aspectRatio(1.5f)
					.fillMaxWidth(),
				PieceType.Black
			)
		}
	}
}

@Composable
@Preview
private fun MarkSamplePreview() {
	ReversiBoardTheme {
		Surface {
			MarkSampleUi(
				Modifier
					.aspectRatio(1.5f)
					.fillMaxWidth(),
			)
		}
	}
}