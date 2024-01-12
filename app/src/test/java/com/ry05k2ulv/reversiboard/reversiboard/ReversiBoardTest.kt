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

    private fun randomBoard(width: Int = 8) = MutableList(width * width) {
        when ((0..2).random()) {
            0 -> Empty
            1 -> Black
            2 -> White
            else -> throw Exception("randomList: Invalid value")
        }
    }

    private fun initialBoard(width: Int = 8) = MutableList(width * width) { Empty }
        .apply {
            this[width / 2 - 1 + (width / 2 - 1) * width] = White
            this[width / 2 - 1 + (width / 2) * width] = Black
            this[width / 2 + (width / 2 - 1) * width] = Black
            this[width / 2 + (width / 2) * width] = White
        }

    @BeforeEach
    fun setup() {
        subject = ReversiBoard()
    }

    @Test
    fun updateBoard() {
        val initialList: List<Piece> = initialBoard()
        subject.updateBoard(initialList)
        Truth.assertThat(subject.elements).isEqualTo(initialList.toList())
    }

    @Test
    fun updateBoard_randomly() {
        repeat(100) {
            val randomList = randomBoard()
            subject.updateBoard(randomList)
            Truth.assertThat(subject.elements).isEqualTo(randomList.toList())
        }
    }

    @Test
    fun updateBoard_throw_error_when_invalid_length() {
        arrayOf(0, 7, 9).forEach { width ->
            val invalidList = randomBoard(width)
            assertThrows<IllegalArgumentException> {
                subject.updateBoard(invalidList)
            }
        }
    }

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

        Truth.assertThat(subject.elements).isEqualTo(board3.toList())
        subject.undo()
        Truth.assertThat(subject.elements).isEqualTo(board2.toList())
        subject.undo()
        Truth.assertThat(subject.elements).isEqualTo(board1.toList())
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

        Truth.assertThat(subject.elements).isEqualTo(board1.toList())
        subject.redo()
        Truth.assertThat(subject.elements).isEqualTo(board2.toList())
        subject.redo()
        Truth.assertThat(subject.elements).isEqualTo(board3.toList())
    }

    @Test
    fun drop() {
        /* Initial
         *   0 1 2 3 4 5 6 7
         * 0
         * 1
         * 2
         * 3       o x
         * 4       x o
         * 5
         * 6
         * 7
         * */
        val initialBoard = initialBoard()
        subject.updateBoard(initialBoard)

        /* First drop
         *   0 1 2 3 4 5 6 7
         * 0
         * 1
         * 2
         * 3       o o o
         * 4       x o
         * 5
         * 6
         * 7
         * */
        val firstBoard = initialBoard.toMutableList()
            .apply {
                this[5 + 3 * 8] = White
                this[4 + 3 * 8] = White
            }
        subject.drop(White, 5, 3)
        Truth.assertThat(firstBoard).isNotEqualTo(initialBoard)
        Truth.assertThat(subject.elements).isEqualTo(firstBoard.toList())

        /* Second drop
         *   0 1 2 3 4 5 6 7
         * 0
         * 1
         * 2       x
         * 3       x o o
         * 4       x o
         * 5
         * 6
         * 7
         * */
        val secondBoard = firstBoard.toMutableList()
            .apply {
                this[3 + 2 * 8] = Black
                this[3 + 3 * 8] = Black
            }
        subject.drop(Black, 3, 2)
        Truth.assertThat(secondBoard).isNotEqualTo(firstBoard)
        Truth.assertThat(subject.elements).isEqualTo(secondBoard.toList())

        /* Third drop
         *   0 1 2 3 4 5 6 7
         * 0
         * 1
         * 2       x
         * 3     o o o o
         * 4       x o
         * 5
         * 6
         * 7
         * */
        val thirdBoard = secondBoard.toMutableList()
            .apply {
                this[2 + 3 * 8] = White
                this[3 + 3 * 8] = White
            }
        subject.drop(White, 2, 3)
        Truth.assertThat(thirdBoard).isNotEqualTo(secondBoard)
        Truth.assertThat(subject.elements).isEqualTo(thirdBoard.toList())

        /* Fourth drop
         *   0 1 2 3 4 5 6 7
         * 0
         * 1
         * 2       x
         * 3     o o x o
         * 4       x x x
         * 5
         * 6
         * 7
         * */
        val fourthBoard = thirdBoard.toMutableList()
            .apply {
                this[4 + 3 * 8] = Black
                this[4 + 4 * 8] = Black
                this[5 + 4 * 8] = Black
            }
        subject.drop(Black, 5, 4)
        Truth.assertThat(fourthBoard).isNotEqualTo(thirdBoard)
        Truth.assertThat(subject.elements).isEqualTo(fourthBoard.toList())
    }

    @Test
    fun reset() {
        val initialBoard = initialBoard()
        subject.updateBoard(randomBoard())
        subject.reset()
        Truth.assertThat(subject.elements).isEqualTo(initialBoard.toList())
    }


    @Test
    fun canUndo_and_undo_is_available_after_reset() {
        val board = randomBoard()
        subject.updateBoard(board)
        subject.reset()
        Truth.assertThat(subject.canUndo()).isTrue()
        subject.undo()
        Truth.assertThat(subject.elements).isEqualTo(board.toList())
        subject.undo()
        Truth.assertThat(subject.canUndo()).isFalse()
    }
}