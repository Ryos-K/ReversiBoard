package com.ry05k2ulv.reversiboard.data.fake

import com.ry05k2ulv.reversiboard.data.BoardSurfaceRepository
import com.ry05k2ulv.reversiboard.reversiboard.BoardSurface
import com.ry05k2ulv.reversiboard.reversiboard.PieceType
import javax.inject.Inject

class FakeBoardSurfaceRepository @Inject constructor() : BoardSurfaceRepository {
	companion object {

		val boardSurfaceList = mutableListOf(
			BoardSurface(
				boardId = 1,
				turn = 1
			),
			BoardSurface(
				boardId = 2,
				turn = 1,
			),
			BoardSurface(
				boardId = 3,
				turn = 1,
			),
			BoardSurface(
				boardId = 4,
				turn = 1,
				elements = randomElements()
			),
			BoardSurface(
				boardId = 4,
				turn = 2,
				elements = randomElements()
			)
		)

		private fun randomElements() = List(64) {
			when ((0..2).random()) {
				0    -> PieceType.Black
				1    -> PieceType.White
				else -> PieceType.Empty
			}
		}
	}

	override fun getBoardSurfaceByIdAndTurn(id: Int, turn: Int): BoardSurface? {
		return boardSurfaceList.find { it.boardId == id && it.turn == turn }
	}

	override fun upsertBoardSurface(boardSurface: BoardSurface) {
		val index =
			boardSurfaceList.indexOfFirst { it.boardId == boardSurface.boardId && it.turn == boardSurface.turn }
		if (index == -1) {
			boardSurfaceList.add(boardSurface)
		} else {
			boardSurfaceList[index] = boardSurface
		}
	}


}