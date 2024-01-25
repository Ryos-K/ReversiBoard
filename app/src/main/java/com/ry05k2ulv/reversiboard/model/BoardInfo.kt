package com.ry05k2ulv.reversiboard.model

import com.ry05k2ulv.reversiboard.ui.components.MarkType

data class BoardInfo(
	val id: Int,
	val title: String,
	val currentTurn: Int,
	val maxTurn: Int,
	val markList: List<MarkType>,
	val readOnly: Boolean,
)
