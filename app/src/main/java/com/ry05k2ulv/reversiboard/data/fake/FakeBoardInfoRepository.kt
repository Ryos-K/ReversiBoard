package com.ry05k2ulv.reversiboard.data.fake

import com.ry05k2ulv.reversiboard.data.BoardInfoRepository
import com.ry05k2ulv.reversiboard.model.BoardInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class FakeBoardInfoRepository @Inject constructor() : BoardInfoRepository {

	val boardInfoList = MutableStateFlow(
		List(4) { boardInfo(it, "foo $it") }
	)

	override fun getBoardInfoList(): Flow<List<BoardInfo>> {
		return boardInfoList
	}

	private fun boardInfo(id: Int, title: String) = BoardInfo(
		id, title, 0, 0, emptyList(), false
	)
}