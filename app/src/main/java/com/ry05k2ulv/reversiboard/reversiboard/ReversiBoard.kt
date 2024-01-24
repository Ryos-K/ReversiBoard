package com.ry05k2ulv.reversiboard.reversiboard

import com.ry05k2ulv.reversiboard.reversiboard.PieceType.*
import java.util.ArrayDeque
import java.util.Stack

const val boardWidth = 8
const val boardArea = boardWidth * boardWidth

internal val dx = arrayOf(0, 1, 1, 1, 0, -1, -1, -1)
internal val dy = arrayOf(1, 1, 0, -1, -1, -1, 0, 1)

operator fun List<PieceType>.get(x: Int, y: Int): PieceType {
    return this[x + y * boardWidth]
}

operator fun MutableList<PieceType>.set(x: Int, y: Int, cell: PieceType) {
    this[x + y * boardWidth] = cell
}

class ReversiBoard(
    private val undoLimit: Int = 1024,
) {
    var boardSurface = BoardSurface()
        private set
    private val undoDeque = ArrayDeque<BoardSurface>()
    private val redoStack = Stack<BoardSurface>()

    operator fun get(x: Int, y: Int) = boardSurface.elements[x, y]

    fun updateBoard(newBoard: BoardSurface) {
        require(newBoard.elements.size == boardArea) { "Invalid length" }
        undoDeque.addLast(boardSurface)
        boardSurface = newBoard
        redoStack.clear()
        if (undoDeque.size > undoLimit)
            undoDeque.removeFirst()
    }

    fun canUndo() = !undoDeque.isEmpty()
    fun canRedo() = !redoStack.isEmpty()

    fun undo() {
        redoStack.push(boardSurface)
        boardSurface = undoDeque.removeLast()
    }

    fun redo() {
        undoDeque.addLast(boardSurface)
        boardSurface = redoStack.pop()
    }

    fun undoAll() {
        redoStack.push(boardSurface)
        boardSurface = undoDeque.removeFirst()
        while (undoDeque.isNotEmpty())
            redoStack.push(undoDeque.removeLast())
    }

    fun redoAll() {
        undoDeque.addLast(boardSurface)
        while (redoStack.isNotEmpty())
            undoDeque.addLast(redoStack.pop())
        boardSurface = undoDeque.removeLast()
    }

    fun drop(piece: Piece): Boolean {
        return boardSurface.dropped(piece)?.let { next ->
            updateBoard(next)
            true
        } ?: false
    }

    fun replace(piece: Piece): Boolean {
        return boardSurface.replaced(piece)?.let { next ->
            updateBoard(next)
            true
        } ?: false
    }

    fun reset() {
        updateBoard(
                BoardSurface(
                        MutableList(boardArea) { Empty }.apply {
                            this[boardWidth / 2 - 1, boardWidth / 2 - 1] = White
                            this[boardWidth / 2 - 1, boardWidth / 2] = Black
                            this[boardWidth / 2, boardWidth / 2 - 1] = Black
                            this[boardWidth / 2, boardWidth / 2] = White
                        }
                )
        )
    }

}

