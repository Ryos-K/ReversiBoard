package com.ry05k2ulv.reversiboard.reversiboard

import com.google.common.truth.Truth
import org.junit.jupiter.api.Test

class BoardSurfaceTest {


    /*
     * x : Black
     * o : White
     * b : Black can drop
     * w : White can drop
     * z : Black and White can drop
     * - : Empty
     * */

    @Test
    fun `canDropList return empty list when board is empty`() {
        val (elements, blackCanDrop, whiteCanDrop) = """
            - - - - - - - -
            - - - - - - - -
            - - - - - - - -
            - - - - - - - -
            - - - - - - - -
            - - - - - - - -
            - - - - - - - -
            - - - - - - - -
        """.trimIndent().toBoard()
        val boardSurface = BoardSurface(elements)
        Truth.assertThat(boardSurface.blackCanDropList).containsExactlyElementsIn(blackCanDrop)
        Truth.assertThat(boardSurface.whiteCanDropList).containsExactlyElementsIn(whiteCanDrop)
    }

    @Test
    fun `canDropList return empty list when board is full`() {
        val (elements, blackCanDrop, whiteCanDrop) = """
            o x o x o x o x
            x o x o x o x o
            o x o x o x o x
            x o x o x o x o
            o x o x o x o x
            x o x o x o x o
            o x o x o x o x
            x o x o x o x o
        """.trimIndent().toBoard()
        val boardSurface = BoardSurface(elements)
        Truth.assertThat(boardSurface.blackCanDropList).containsExactlyElementsIn(blackCanDrop)
        Truth.assertThat(boardSurface.whiteCanDropList).containsExactlyElementsIn(whiteCanDrop)
    }

    @Test
    fun `canDropList return correct list in the begging`() {
        val (elements, blackCanDrop, whiteCanDrop) = """
            - - - - - - - -
            - - - - - - - -
            - - - b w - - -
            - - b o x w - -
            - - w x o b - -
            - - - w b - - -   
            - - - - - - - -
            - - - - - - - -
        """.trimIndent().toBoard()
        val boardSurface = BoardSurface(elements)
        Truth.assertThat(boardSurface.blackCanDropList).containsExactlyElementsIn(blackCanDrop)
        Truth.assertThat(boardSurface.whiteCanDropList).containsExactlyElementsIn(whiteCanDrop)
    }

    @Test
    fun `canDropList return correct list in the middle`() {
        val (elements, blackCanDrop, whiteCanDrop) = """
            - - b z w z - -
            - b b o x o b -
            - b o o o o o -
            - b o o o o x w
            - b o o o x x w
            - b o o x x w w
            - - - x x x w -
            - - x w w w w -
        """.trimIndent().toBoard()
        val boardSurface = BoardSurface(elements)
        Truth.assertThat(boardSurface.blackCanDropList).containsExactlyElementsIn(blackCanDrop)
        Truth.assertThat(boardSurface.whiteCanDropList).containsExactlyElementsIn(whiteCanDrop)
    }

    @Test
    fun `canDropList return correct list in the end`() {
        val (elements, blackCanDrop, whiteCanDrop) = """
            w x o x x x w -
            z x x x x x o b
            o x x x x o o x
            x x x o o x o x
            o o x o o x o x
            o o o o o o o x
            b z x o o x o b
            - w x x o o z b
        """.trimIndent().toBoard()
        val boardSurface = BoardSurface(elements)
        Truth.assertThat(boardSurface.blackCanDropList).containsExactlyElementsIn(blackCanDrop)
        Truth.assertThat(boardSurface.whiteCanDropList).containsExactlyElementsIn(whiteCanDrop)
    }
}