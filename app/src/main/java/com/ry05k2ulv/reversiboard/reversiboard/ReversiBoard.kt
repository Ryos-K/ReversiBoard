package com.ry05k2ulv.reversiboard.reversiboard

import com.ry05k2ulv.reversiboard.reversiboard.Piece.*
import java.util.ArrayDeque
import java.util.Deque
import java.util.LinkedList
import java.util.Stack

typealias Board = List<Piece>

class ReversiBoard(
    val length: Int = 8,
    val undoLimit: Int = 1024
) {
    val dx = arrayOf(0, 1, 1, 1, 0, -1, -1, -1)
    val dy = arrayOf(1, 1, 0, -1, -1, -1, 0, 1)

    var board: Board = List(length * length) { Empty }
    private val undoDeque = ArrayDeque<Board>()
    private val redoStack = Stack<Board>()

    fun updateBoard(newBoard: Board) {
        undoDeque.addLast(board)
        board = newBoard
        redoStack.clear()
        if (undoDeque.size > undoLimit)
            undoDeque.removeFirst()
    }

    fun canUndo() = !undoDeque.isEmpty()
    fun canRedo() = !redoStack.isEmpty()
    fun undo() {
        redoStack.push(board)
        board = undoDeque.pop()
    }
    fun redo() {
        undoDeque.addLast(board)
        board = redoStack.pop()
    }

    fun drop(piece: Piece, x: Int, y: Int, overwrite: Boolean, reversible: Boolean) {
        val nextBoard = board.toMutableList()
        if (nextBoard[x, y] != Empty && !overwrite) return
        nextBoard[x, y] = piece
        if (!reversible) return
        /* nextBoard[x, y] == Empty && reversible */
        repeat(8) { i ->
            var j = 1
            while (true) {
                val nextX = x + dx[j]
                val nextY = y + dy[j]
                if (
                    nextX !in 0 until length ||
                    nextY !in 0 until length ||
                    nextBoard[nextX, nextY] == Empty
                ) return@repeat
                if (nextBoard[nextX, nextY] == piece)
                    break
                j++
            }
            while (--j > 0)
                nextBoard[x + dx[j], y + dy[j]] = piece
        }
        updateBoard(nextBoard)
    }

    fun reset() {
        val nextBoard = board.toMutableList()
        repeat(length * length) { nextBoard[it] = Empty }
        nextBoard[length / 2 - 1, length / 2 - 1] = White
        nextBoard[length / 2 - 1, length / 2] = Black
        nextBoard[length / 2, length / 2 - 1] = Black
        nextBoard[length / 2, length / 2] = White
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