package com.ry05k2ulv.reversiboard.data

import com.ry05k2ulv.reversiboard.reversiboard.BoardSurface

interface BoardSurfaceRepository {
	fun getBoardSurfaceByIdAndTurn(id: Int, turn: Int): BoardSurface?

	fun upsertBoardSurface(boardSurface: BoardSurface)
}