@file:OptIn(ExperimentalMaterial3Api::class)

package com.ry05k2ulv.reversiboard.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController
import com.ry05k2ulv.reversiboard.R
import com.ry05k2ulv.reversiboard.model.BoardInfo
import com.ry05k2ulv.reversiboard.navigation.RbNavHost
import com.ry05k2ulv.reversiboard.ui.home.*
import com.ry05k2ulv.reversiboard.ui.settings.SettingsDialog
import kotlinx.coroutines.launch

@Composable
fun RbApp(
	viewModel: RbAppViewModel = hiltViewModel()
) {
	val scope = rememberCoroutineScope()

	val uiState by viewModel.uiState.collectAsState()

	val navController = rememberNavController()

	var showSettingsDialog by remember { mutableStateOf(false) }
	if (showSettingsDialog) {
		SettingsDialog(
			onDismiss = { showSettingsDialog = false }
		)
	}

	var selectedBoardId by remember {
		mutableIntStateOf(1)
	}

	val drawerState = rememberDrawerState(DrawerValue.Closed)
	ModalNavigationDrawer(
		drawerContent = {
			RbModalDrawerSheet(
				boardInfoList = when (val s = uiState) {
					RbAppUiState.Loading -> emptyList()
					is RbAppUiState.Success -> s.boardInfoList
				},
				onBoardInfoClick = {
					selectedBoardId = it.id
					navController.navigateToHome(
						it.id,
						NavOptions.Builder()
							.setPopUpTo("$homeNavigationRoute?$idArg=${it.id}", true).build()
					)
				},
				isLoading = uiState == RbAppUiState.Loading,
				selectedId = selectedBoardId,
				modifier = Modifier.fillMaxWidth(0.65f),
			)
		},
		drawerState = drawerState,
	) {
		Scaffold(
			topBar = {
				RbTopAppBar(
					title = stringResource(R.string.app_name),
					navigationIcon = Icons.Default.Menu,
					navigationIconDescription = "Menu",
					onNavigationIconClick = { scope.launch { drawerState.open() } },
					actionIcon = Icons.Default.Settings,
					actionIconDescription = "Settings",
					onAction = { showSettingsDialog = true }
				)
			},
		) {
			RbNavHost(
				Modifier.padding(it),
				startDestination = "$homeNavigationRoute",
				navController = navController
			)
		}
	}
}

@Composable
private fun RbModalDrawerSheet(
	boardInfoList: List<BoardInfo>,
	selectedId: Int,
	onBoardInfoClick: (BoardInfo) -> Unit,
	modifier: Modifier = Modifier,
	isLoading: Boolean = false,
) {
	ModalDrawerSheet(
		modifier
	) {
		Spacer(Modifier.height(32.dp))
		DrawerSheetSectionTitle(
			Modifier
				.padding(16.dp, 4.dp)
				.height(48.dp),
			title = "Board List"
		)

		if (isLoading) {
			CircularProgressIndicator()
		} else {
			boardInfoList.forEach {
				DrawerSheetItemRow(
					Modifier
						.padding(16.dp, 4.dp)
						.height(32.dp),
					boardInfo = it,
					selected = it.id == selectedId,
					onClick = { onBoardInfoClick(it) })
			}
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

@Composable
private fun DrawerSheetSectionTitle(
	modifier: Modifier,
	title: String
) {
	Text(
		title,
		modifier = modifier,
		style = MaterialTheme.typography.headlineMedium
	)
}

@Composable
private fun DrawerSheetItemRow(
	modifier: Modifier,
	boardInfo: BoardInfo,
	selected: Boolean,
	onClick: () -> Unit
) {
	val contentColor = animateColorAsState(
		if (selected) MaterialTheme.colorScheme.primary
		else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
		label = ""
	)
	val bulletWidth = animateDpAsState(
		if (selected) 24.dp else 0.dp, label = ""
	)
	// text with bullet
	Row(
		modifier = modifier
			.clickable { onClick() }
	) {
		Icon(
			imageVector = Icons.Default.KeyboardArrowRight,
			contentDescription = null,
			modifier.width(bulletWidth.value),
			tint = contentColor.value
		)
		Text(
			text = boardInfo.title,
			style = MaterialTheme.typography.titleMedium,
			color = contentColor.value
		)
	}
}