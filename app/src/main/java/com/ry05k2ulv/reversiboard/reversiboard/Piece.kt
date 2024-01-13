package com.ry05k2ulv.reversiboard.reversiboard

import com.ry05k2ulv.reversiboard.reversiboard.Piece.Empty
import com.ry05k2ulv.reversiboard.reversiboard.Piece.Black
import com.ry05k2ulv.reversiboard.reversiboard.Piece.White

enum class Piece {
    Empty,
    Black,
    White,
}

fun Piece.isEmpty(): Boolean = this == Empty

fun Piece.opposite(): Piece = when (this) {
    Empty -> Empty
    Black -> White
    White -> Black
}