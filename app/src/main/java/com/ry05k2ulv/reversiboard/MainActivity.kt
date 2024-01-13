package com.ry05k2ulv.reversiboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ry05k2ulv.reversiboard.ui.home.HomeScreen
import com.ry05k2ulv.reversiboard.ui.home.HomeViewModel
import com.ry05k2ulv.reversiboard.ui.theme.ReversiBoardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReversiBoardTheme {
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

