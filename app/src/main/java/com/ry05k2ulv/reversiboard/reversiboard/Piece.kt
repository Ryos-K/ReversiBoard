package com.ry05k2ulv.reversiboard.reversiboard

import com.ry05k2ulv.reversiboard.reversiboard.PieceType.Black
import com.ry05k2ulv.reversiboard.reversiboard.PieceType.Empty
import com.ry05k2ulv.reversiboard.reversiboard.PieceType.White

enum class PieceType {
    Empty,
    Black,
    White,
}

data class Piece(
        val type: PieceType,
        val x: Int,
        val y: Int,
)

fun PieceType.isEmpty(): Boolean = this == Empty

fun PieceType.opposite(): PieceType = when (this) {
    Empty -> Empty
    Black -> White
    White -> Black
}