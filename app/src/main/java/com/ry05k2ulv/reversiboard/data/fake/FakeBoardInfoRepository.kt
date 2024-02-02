package com.ry05k2ulv.reversiboard.data.fake

import com.ry05k2ulv.reversiboard.data.BoardInfoRepository
import com.ry05k2ulv.reversiboard.model.BoardInfo
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.random.Random

class FakeBoardInfoRepository @Inject constructor() : BoardInfoRepository {

	companion object {
		val boardInfoList = MutableStateFlow(
			List(4) { boardInfo(it + 1, "foo $it") }
		)

		private fun boardInfo(id: Int, title: String) = BoardInfo(
			id, title, 1, 1, emptyList(), false
		)
	}

	override fun getBoardInfoList(): Flow<List<BoardInfo>> {
		return boardInfoList
	}

	override fun getBoardInfoById(id: Int): Flow<BoardInfo> {
		return boardInfoList.map { it.find { boardInfo -> boardInfo.id == id }!! }
	}

	override fun insertBoardInfo(boardInfo: BoardInfo): Int {
		val id = Random.nextInt()
		boardInfoList.update {
			it + boardInfo.copy(
				id = id
			)
		}
		return id
	}

	override fun updateBoardInfo(boardInfo: BoardInfo) {
		boardInfoList.update {
			it.map { oldBoardInfo ->
				if (oldBoardInfo.id == boardInfo.id) {
					boardInfo
				} else {
					oldBoardInfo
				}
			}
		}
	}


}