package com.ry05k2ulv.reversiboard.reversiboard

import com.ry05k2ulv.reversiboard.reversiboard.Piece.*
import java.util.ArrayDeque
import java.util.Stack

typealias PieceList = List<Piece>

class ReversiBoard(
    val width: Int = 8,
    val undoLimit: Int = 1024,
) {
    private val dx = arrayOf(0, 1, 1, 1, 0, -1, -1, -1)
    private val dy = arrayOf(1, 1, 0, -1, -1, -1, 0, 1)

    var elements: PieceList = List(width * width) { Empty }
        private set
    private val undoDeque = ArrayDeque<PieceList>()
    private val redoStack = Stack<PieceList>()

    operator fun get(x: Int, y: Int) = elements[x, y]

    fun updateBoard(newBoard: PieceList) {
        require(newBoard.size == width * width) { "Invalid length" }
        undoDeque.addLast(elements)
        elements = newBoard
        redoStack.clear()
        if (undoDeque.size > undoLimit)
            undoDeque.removeFirst()
    }

    fun canUndo() = !undoDeque.isEmpty()
    fun canRedo() = !redoStack.isEmpty()
    fun undo() {
        redoStack.push(elements)
        elements = undoDeque.removeLast()
    }
    fun redo() {
        undoDeque.addLast(elements)
        elements = redoStack.pop()
    }

    fun drop(piece: Piece, x: Int, y: Int, overwrite: Boolean = false, reversible: Boolean = true) {
        val nextBoard = elements.toMutableList()
        if (nextBoard[x, y] != Empty && !overwrite) return
        nextBoard[x, y] = piece
        if (!reversible) return
        /* nextBoard[x, y] == Empty && reversible */
        repeat(8) { i ->
            var j = 1
            while (true) {
                val nextX = x + dx[i] * j
                val nextY = y + dy[i] * j
                if (
                    nextX !in 0 until width ||
                    nextY !in 0 until width ||
                    nextBoard[nextX, nextY] == Empty
                ) return@repeat
                if (nextBoard[nextX, nextY] == piece)
                    break
                j++
            }
            while (--j > 0)
                nextBoard[x + dx[i] * j, y + dy[i] * j] = piece
        }
        updateBoard(nextBoard)
    }

    fun reset() {
        val nextBoard = elements.toMutableList()
        repeat(width * width) { nextBoard[it] = Empty }
        nextBoard[width / 2 - 1, width / 2 - 1] = White
        nextBoard[width / 2 - 1, width / 2] = Black
        nextBoard[width / 2, width / 2 - 1] = Black
        nextBoard[width / 2, width / 2] = White
        updateBoard(nextBoard)
    }

    private operator fun List<Piece>.get(x: Int, y: Int): Piece {
        return this[x + y * 8]
    }

    private operator fun MutableList<Piece>.set(x: Int, y: Int, cell: Piece) {
        this[x + y * 8] = cell
    }
}

enum class Piece {
    Empty,
    Black,
    White,
}

val Piece.opposite: Piece
    get() = when (this) {
        Empty -> Empty
        Black -> White
        White -> Black
    }