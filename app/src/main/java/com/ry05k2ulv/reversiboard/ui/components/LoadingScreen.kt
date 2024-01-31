package com.ry05k2ulv.reversiboard.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoadingScreen(modifier: Modifier) {
	Box(modifier) {
		CircularProgressIndicator()
	}
}