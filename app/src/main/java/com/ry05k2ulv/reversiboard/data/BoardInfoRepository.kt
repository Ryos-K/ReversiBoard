package com.ry05k2ulv.reversiboard.data

import com.ry05k2ulv.reversiboard.model.BoardInfo
import kotlinx.coroutines.flow.Flow

interface BoardInfoRepository {
	fun getBoardInfoList(): Flow<List<BoardInfo>>

	fun getBoardInfoById(id: Int): Flow<BoardInfo>

	fun insertBoardInfo(boardInfo: BoardInfo): Int

	fun updateBoardInfo(boardInfo: BoardInfo)
}