package com.ry05k2ulv.reversiboard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ry05k2ulv.reversiboard.ui.home.homeScreen

@Composable
fun RbNavHost(
	modifier: Modifier,
	startDestination: String,
	navController: NavHostController = rememberNavController(),
	navOptions: NavOptions? = null
) {
	NavHost(
		navController = navController,
		startDestination = startDestination,
		modifier = modifier
	) {
		homeScreen()
	}
}