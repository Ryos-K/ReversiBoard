package com.ry05k2ulv.reversiboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ry05k2ulv.reversiboard.model.DarkThemeConfig
import com.ry05k2ulv.reversiboard.ui.home.HomeScreen
import com.ry05k2ulv.reversiboard.ui.home.HomeViewModel
import com.ry05k2ulv.reversiboard.ui.theme.ReversiBoardTheme

class MainActivity : ComponentActivity() {

    val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            ReversiBoardTheme(
                darkTheme = darkThemeConfig(uiState),
                dynamicColor = useDynamicColor(uiState)
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel = HomeViewModel()
                    HomeScreen(viewModel)
                }
            }
        }
    }
}

@Composable
private fun darkThemeConfig(uiState: MainActivityUiState) =
    when (uiState) {
        MainActivityUiState.Loading -> isSystemInDarkTheme()
        is MainActivityUiState.Success -> when (uiState.userData.darkThemeConfig) {
            DarkThemeConfig.SYSTEM -> isSystemInDarkTheme()
            DarkThemeConfig.LIGHT  -> false
            DarkThemeConfig.DARK -> true
        }
    }

@Composable
private fun useDynamicColor(uiState: MainActivityUiState) =
    when (uiState) {
        MainActivityUiState.Loading -> false
        is MainActivityUiState.Success -> uiState.userData.useDynamicColor
    }