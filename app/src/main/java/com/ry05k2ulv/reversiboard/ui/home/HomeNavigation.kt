package com.ry05k2ulv.reversiboard.ui.home

import androidx.navigation.*
import androidx.navigation.compose.composable

const val homeNavigationRoute = "home"
const val idArg = "id"

fun NavController.navigateToHome(id: Int, navOptions: NavOptions? = null) {
	this.navigate("$homeNavigationRoute?$idArg=$id", navOptions)
}

fun NavGraphBuilder.homeScreen() {
	composable(
		route = "$homeNavigationRoute?$idArg={$idArg}",
		arguments = listOf(
			navArgument(idArg) {
				type = NavType.IntType
				defaultValue = 1
			}
		)
	) {
		HomeScreen()
	}
}

