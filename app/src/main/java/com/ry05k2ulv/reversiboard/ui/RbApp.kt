@file:OptIn(ExperimentalMaterial3Api::class)

package com.ry05k2ulv.reversiboard.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.ry05k2ulv.reversiboard.R
import com.ry05k2ulv.reversiboard.ui.home.HomeScreen
import com.ry05k2ulv.reversiboard.ui.settings.SettingsDialog

@Composable
fun RbApp() {
	var showSettingsDialog by remember { mutableStateOf(false) }
	if (showSettingsDialog) {
		SettingsDialog(
			onDismiss = { showSettingsDialog = false }
		)
	}

	Scaffold(
		topBar = {
			RbTopAppBar(
				title = stringResource(R.string.app_name),
				navigationIcon = Icons.Default.Menu,
				navigationIconDescription = "Menu",
				onNavigationIconClick = {},
				actionIcon = Icons.Default.Settings,
				actionIconDescription = "Settings",
				onAction = { showSettingsDialog = true }
			)
		},
	) {
		Box(
			Modifier.padding(it)
		) {
			HomeScreen()
		}
	}
}

@Composable
private fun RbTopAppBar(
	title: String,
	navigationIcon: ImageVector,
	navigationIconDescription: String,
	onNavigationIconClick: () -> Unit,
	actionIcon: ImageVector,
	actionIconDescription: String,
	onAction: () -> Unit,
	colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
		containerColor = MaterialTheme.colorScheme.primary,
		navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
		actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
		titleContentColor = MaterialTheme.colorScheme.onPrimary
	)
) {
	CenterAlignedTopAppBar(
		title = {
			Text(title)
		},
		navigationIcon = {
			IconButton(onClick = onNavigationIconClick) {
				Icon(imageVector = navigationIcon, contentDescription = navigationIconDescription)
			}
		},
		actions = {
			IconButton(onClick = onAction) {
				Icon(imageVector = actionIcon, contentDescription = actionIconDescription)
			}
		},
		colors = colors
	)
}