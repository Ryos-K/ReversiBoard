package com.ry05k2ulv.reversiboard.reversiboard

private fun elementsDefault() = MutableList(boardArea) { PieceType.Empty }.apply {
    this[boardWidth / 2 - 1, boardWidth / 2 - 1] = PieceType.White
    this[boardWidth / 2 - 1, boardWidth / 2] = PieceType.Black
    this[boardWidth / 2, boardWidth / 2 - 1] = PieceType.Black
    this[boardWidth / 2, boardWidth / 2] = PieceType.White
}

data class BoardSurface(
        val elements: List<PieceType> = elementsDefault(),
        val expectPieceType: PieceType = PieceType.Black,
        val lastPiece: Piece? = null,
) {
    val blackCanDropList: List<Int> = canDropList(PieceType.Black)
    val whiteCanDropList: List<Int> = canDropList(PieceType.White)

    fun dropped(piece: Piece): BoardSurface? {
        return if (canDrop(piece)) {
            val newElements = elements.toMutableList()
            newElements.reverseBy(piece)
            BoardSurface(newElements, piece.type.opposite(), piece)
        } else null
    }

    fun replaced(piece: Piece): BoardSurface? {
        return if (elements[piece.x, piece.y] != piece.type) {
            val newElements = elements.toMutableList()
            newElements[piece.x, piece.y] = piece.type
            BoardSurface(newElements, piece.type, piece)
        } else null
    }

    fun canDrop(piece: Piece): Boolean {
        val canDropList = when (piece.type) {
            PieceType.Black -> blackCanDropList
            PieceType.White -> whiteCanDropList
            else -> emptyList()
        }
        return piece.x + piece.y * boardWidth in canDropList
    }

    private fun canDropList(pieceType: PieceType): List<Int> {
        val canDropList = mutableListOf<Int>()
        repeat(boardArea) { i ->
            if (!elements[i].isEmpty()) return@repeat
            val x = i % boardWidth
            val y = i / boardWidth
            repeat(8) canDrop@{ j ->
                var k = 1
                while (true) {
                    val nextX = x + dx[j] * k
                    val nextY = y + dy[j] * k
                    if (
                            nextX !in 0 until boardWidth ||
                            nextY !in 0 until boardWidth ||
                            elements[nextX, nextY].isEmpty()
                    ) return@canDrop
                    if (elements[nextX, nextY] == pieceType)
                        break
                    k++
                }
                if (k > 1) {
                    canDropList.add(i)
                    return@repeat
                }
            }
        }
        return canDropList
    }

    private fun MutableList<PieceType>.reverseBy(piece: Piece) {
        this[piece.x, piece.y] = piece.type
        repeat(8) { i ->
            var j = 1
            while (true) {
                val nextX = piece.x + dx[i] * j
                val nextY = piece.y + dy[i] * j
                if (
                        nextX !in 0 until boardWidth ||
                        nextY !in 0 until boardWidth ||
                        this[nextX, nextY].isEmpty()
                ) return@repeat
                if (this[nextX, nextY] == piece.type)
                    break
                j++
            }
            while (--j > 0)
                this[piece.x + dx[i] * j, piece.y + dy[i] * j] = piece.type
        }
    }
}
