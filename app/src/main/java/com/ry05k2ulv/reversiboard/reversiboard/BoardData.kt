package com.ry05k2ulv.reversiboard.reversiboard

private fun elementsDefault() = MutableList(boardArea) { Piece.Empty }.apply {
    this[boardWidth / 2 - 1, boardWidth / 2 - 1] = Piece.White
    this[boardWidth / 2 - 1, boardWidth / 2] = Piece.Black
    this[boardWidth / 2, boardWidth / 2 - 1] = Piece.Black
    this[boardWidth / 2, boardWidth / 2] = Piece.White
}

data class BoardData(
    val elements: List<Piece> = elementsDefault()
) {
    val blackCanDropList: List<Int> = canDropList(Piece.Black)
    val whiteCanDropList: List<Int> = canDropList(Piece.White)

    private fun canDropList(piece: Piece): List<Int> {
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
                    if (elements[nextX, nextY] == piece)
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
}
