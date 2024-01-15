package com.ry05k2ulv.reversiboard.reversiboard

import com.ry05k2ulv.reversiboard.reversiboard.Piece.*
import java.util.ArrayDeque
import java.util.Stack

const val boardWidth = 8
const val boardArea = boardWidth * boardWidth

internal val dx = arrayOf(0, 1, 1, 1, 0, -1, -1, -1)
internal val dy = arrayOf(1, 1, 0, -1, -1, -1, 0, 1)

operator fun List<Piece>.get(x: Int, y: Int): Piece {
    return this[x + y * boardWidth]
}

operator fun MutableList<Piece>.set(x: Int, y: Int, cell: Piece) {
    this[x + y * boardWidth] = cell
}

class ReversiBoard(
    val undoLimit: Int = 1024,
) {


    var boardData = BoardData()
        private set
    private val undoDeque = ArrayDeque<BoardData>()
    private val redoStack = Stack<BoardData>()

    operator fun get(x: Int, y: Int) = boardData.elements[x, y]

    fun updateBoard(newBoard: BoardData) {
        require(newBoard.elements.size == boardArea) { "Invalid length" }
        undoDeque.addLast(boardData)
        boardData = newBoard
        redoStack.clear()
        if (undoDeque.size > undoLimit)
            undoDeque.removeFirst()
    }

    fun canUndo() = !undoDeque.isEmpty()
    fun canRedo() = !redoStack.isEmpty()

    fun undo() {
        redoStack.push(boardData)
        boardData = undoDeque.removeLast()
    }

    fun redo() {
        undoDeque.addLast(boardData)
        boardData = redoStack.pop()
    }

    fun undoAll() {
        redoStack.push(boardData)
        boardData = undoDeque.removeFirst()
        while (undoDeque.isNotEmpty())
            redoStack.push(undoDeque.removeLast())
    }

    fun redoAll() {
        undoDeque.addLast(boardData)
        while (redoStack.isNotEmpty())
            undoDeque.addLast(redoStack.pop())
        boardData = undoDeque.removeLast()
    }

    fun drop(
        piece: Piece,
        x: Int,
        y: Int,
        overwrite: Boolean = false,
        reversible: Boolean = true,
    ): Boolean {
        val canDropList = when (piece) {
            Black -> boardData.blackCanDropList
            White -> boardData.whiteCanDropList
            else  -> emptyList()
        }
        return if (
            overwrite ||
            x + y * boardWidth in canDropList
        ) {
            val elements = boardData.elements.toMutableList()
            elements[x, y] = piece
            if (reversible)
                elements.reverse(piece, x, y)
            updateBoard(BoardData(elements))
            true
        } else {
            false
        }
    }

    fun reset() {
        updateBoard(
            BoardData(
                MutableList(boardArea) { Empty }.apply {
                    this[boardWidth / 2 - 1, boardWidth / 2 - 1] = White
                    this[boardWidth / 2 - 1, boardWidth / 2] = Black
                    this[boardWidth / 2, boardWidth / 2 - 1] = Black
                    this[boardWidth / 2, boardWidth / 2] = White
                }
            )
        )
    }

    private fun MutableList<Piece>.reverse(piece: Piece, x: Int, y: Int) {
        this[x, y] = piece
        repeat(8) { i ->
            var j = 1
            while (true) {
                val nextX = x + dx[i] * j
                val nextY = y + dy[i] * j
                if (
                    nextX !in 0 until boardWidth ||
                    nextY !in 0 until boardWidth ||
                    this[nextX, nextY].isEmpty()
                ) return@repeat
                if (this[nextX, nextY] == piece)
                    break
                j++
            }
            while (--j > 0)
                this[x + dx[i] * j, y + dy[i] * j] = piece
        }
    }
}

