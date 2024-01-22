package com.ry05k2ulv.reversiboard.reversiboard

fun dim2to1(x: Int, y: Int): Int = x + y * boardWidth

// return (elements, blackCanDropList, whiteCanDropList)
fun String.toBoard(): Triple<List<PieceType>, List<Int>, List<Int>> {
	val elements = MutableList(boardArea) { PieceType.Empty }
	val blackCanDropList = mutableListOf<Int>()
	val whiteCanDropList = mutableListOf<Int>()
	var i = 0
	this.forEach { c ->
		when (c) {
			'x' -> elements[i++] = PieceType.Black
			'o' -> elements[i++] = PieceType.White
			'b' -> blackCanDropList.add(i++)
			'w' -> whiteCanDropList.add(i++)
			'z' -> {
				blackCanDropList.add(i)
				whiteCanDropList.add(i++)
			}

			'-' -> i++

			else -> Unit
		}
    }
    return Triple(elements, blackCanDropList, whiteCanDropList)
}