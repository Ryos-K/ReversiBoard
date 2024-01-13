package com.ry05k2ulv.reversiboard.reversiboard

import com.google.common.truth.Truth
import com.ry05k2ulv.reversiboard.reversiboard.Piece.Black
import com.ry05k2ulv.reversiboard.reversiboard.Piece.Empty
import com.ry05k2ulv.reversiboard.reversiboard.Piece.White
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ReversiBoardTest {

    private lateinit var subject: ReversiBoard

    private fun randomBoard(width: Int = 8) = BoardData(
        MutableList(width * width) {
            when ((0..2).random()) {
                0 -> Empty
                1 -> Black
                2 -> White
                else -> throw Exception("randomList: Invalid value")
            }
        }
    )

    private fun initialBoard(width: Int = 8) = BoardData(
        MutableList(width * width) { Empty }
            .apply {
                this[width / 2 - 1 + (width / 2 - 1) * width] = White
                this[width / 2 - 1 + (width / 2) * width] = Black
                this[width / 2 + (width / 2 - 1) * width] = Black
                this[width / 2 + (width / 2) * width] = White
            }
    )

    @BeforeEach
    fun setup() {
        subject = ReversiBoard()
    }

    @Test
    fun updateBoard() {
        val initialBoard = initialBoard()
        subject.updateBoard(initialBoard)
        Truth.assertThat(subject.boardData).isEqualTo(initialBoard.copy())
    }

    @Test
    fun updateBoard_randomly() {
        repeat(100) {
            val randomBoard = randomBoard()
            subject.updateBoard(randomBoard)
            Truth.assertThat(subject.boardData).isEqualTo(randomBoard.copy())
        }
    }

    // this case is used at adding flexible length board
//    @Test
//    fun updateBoard_throw_error_when_invalid_length() {
//        arrayOf(0, 7, 9).forEach { width ->
//            val invalidList = randomBoard(width)
//            assertThrows<IllegalArgumentException> {
//                subject.updateBoard(invalidList)
//            }
//        }
//    }

    @Test
    fun canUndo_return_false_initially() {
        Truth.assertThat(subject.canUndo()).isFalse()
    }

    @Test
    fun canUndo_return_true_after_updateBoard() {
        subject.updateBoard(randomBoard())
        Truth.assertThat(subject.canUndo()).isTrue()
    }

    @Test
    fun canRedo_return_false_initially() {
        Truth.assertThat(subject.canRedo()).isFalse()
    }

    @Test
    fun canRedo_return_false_after_updateBoard() {
        subject.updateBoard(randomBoard())
        Truth.assertThat(subject.canRedo()).isFalse()
    }

    @Test
    fun undo_after_updateBoard() {
        val board1 = randomBoard()
        val board2 = randomBoard()
        val board3 = randomBoard()
        subject.updateBoard(board1)
        subject.updateBoard(board2)
        subject.updateBoard(board3)

        Truth.assertThat(subject.boardData).isEqualTo(board3.copy())
        subject.undo()
        Truth.assertThat(subject.boardData).isEqualTo(board2.copy())
        subject.undo()
        Truth.assertThat(subject.boardData).isEqualTo(board1.copy())
    }

    @Test
    fun redo_after_updateBoard_and_undo() {
        val board1 = randomBoard()
        val board2 = randomBoard()
        val board3 = randomBoard()
        subject.updateBoard(board1)
        subject.updateBoard(board2)
        subject.updateBoard(board3)
        subject.undo()
        subject.undo()

        Truth.assertThat(subject.boardData).isEqualTo(board1.copy())
        subject.redo()
        Truth.assertThat(subject.boardData).isEqualTo(board2.copy())
        subject.redo()
        Truth.assertThat(subject.boardData).isEqualTo(board3.copy())
    }

    @Test
    fun drop() {
        val initialBoard = initialBoard()
        subject.updateBoard(initialBoard)

        // First drop
        val firstBoard = BoardData(
            """
                - - - - - - - -
                - - - - - - - -
                - - - - - - - -
                - - - o o o - -
                - - - x o - - -
                - - - - - - - -
                - - - - - - - -
            """.trimIndent().toBoard().first
        )
        subject.drop(White, 5, 3)
        Truth.assertThat(firstBoard).isNotEqualTo(initialBoard)
        Truth.assertThat(subject.boardData).isEqualTo(firstBoard.copy())

        // Second drop
        val secondBoard = BoardData(
            """
                - - - - - - - -
                - - - - - - - -
                - - - x - - - -
                - - - x o o - -
                - - - x o - - -
                - - - - - - - -
                - - - - - - - -
            """.trimIndent().toBoard().first
        )
        subject.drop(Black, 3, 2)
        Truth.assertThat(secondBoard).isNotEqualTo(firstBoard)
        Truth.assertThat(subject.boardData).isEqualTo(secondBoard.copy())

        val thirdBoard = BoardData(
            """
                - - - - - - - -
                - - - - - - - -
                - - - x - - - -
                - - o o o o - -
                - - - x o - - -
                - - - - - - - -
                - - - - - - - -
            """.trimIndent().toBoard().first
        )
        subject.drop(White, 2, 3)
        Truth.assertThat(thirdBoard).isNotEqualTo(secondBoard)
        Truth.assertThat(subject.boardData).isEqualTo(thirdBoard.copy())

        val fourthBoard = BoardData(
            """
                - - - - - - - -
                - - - - - - - -
                - - - x - - - -
                - - o o x o - -
                - - - x x x - -
                - - - - - - - -
                - - - - - - - -
            """.trimIndent().toBoard().first
        )
        subject.drop(Black, 5, 4)
        Truth.assertThat(fourthBoard).isNotEqualTo(thirdBoard)
        Truth.assertThat(subject.boardData).isEqualTo(fourthBoard.copy())
    }

    @Test
    fun reset() {
        val initialBoard = initialBoard()
        subject.updateBoard(randomBoard())
        subject.reset()
        Truth.assertThat(subject.boardData).isEqualTo(initialBoard.copy())
    }


    @Test
    fun canUndo_and_undo_is_available_after_reset() {
        val board = randomBoard()
        subject.updateBoard(board)
        subject.reset()
        Truth.assertThat(subject.canUndo()).isTrue()
        subject.undo()
        Truth.assertThat(subject.boardData).isEqualTo(board.copy())
        subject.undo()
        Truth.assertThat(subject.canUndo()).isFalse()
    }
}