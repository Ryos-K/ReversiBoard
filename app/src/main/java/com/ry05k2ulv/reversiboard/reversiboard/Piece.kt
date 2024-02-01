package com.ry05k2ulv.reversiboard.reversiboard

import com.ry05k2ulv.reversiboard.reversiboard.PieceType.*

enum class PieceType {
    Empty,
    Black,
    White,
    Block,
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
    Block -> Block
}