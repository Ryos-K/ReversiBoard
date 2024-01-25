@file:OptIn(ExperimentalMaterial3Api::class)

package com.ry05k2ulv.reversiboard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
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
import com.ry05k2ulv.reversiboard.ui.home.homeNavigationRoute
import com.ry05k2ulv.reversiboard.ui.home.navigateToHome
import com.ry05k2ulv.reversiboard.ui.settings.SettingsDialog
import kotlinx.coroutines.launch

@Composable
fun RbApp(
	viewModel: RbAppViewModel = hiltViewModel()
) {
	val scope = rememberCoroutineScope()

	val uiState by viewModel.uiState.collectAsState()

	val navController = rememberNavController()
	val navOptions = remember {
		NavOptions.Builder()
			.setLaunchSingleTop(true)
			.build()
	}

	var showSettingsDialog by remember { mutableStateOf(false) }
	if (showSettingsDialog) {
		SettingsDialog(
			onDismiss = { showSettingsDialog = false }
		)
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
					navController.navigateToHome(it.id, navOptions)
				},
				isLoading = uiState == RbAppUiState.Loading
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
	onBoardInfoClick: (BoardInfo) -> Unit,
	modifier: Modifier = Modifier,
	isLoading: Boolean = false,
) {
	ModalDrawerSheet(
		modifier
	) {
		Text("Board List", style = MaterialTheme.typography.headlineMedium)

		if (isLoading) {
			CircularProgressIndicator()
		} else {
			boardInfoList.forEach {
				Row(
					Modifier
						.padding(16.dp, 8.dp)
						.height(48.dp)
						.clickable { onBoardInfoClick(it) }
				) {
					Text(it.title, style = MaterialTheme.typography.labelLarge)
				}
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