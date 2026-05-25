package com.xiaoxisi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.xiaoxisi.ui.main.MainScreen
import com.xiaoxisi.ui.settings.SettingsScreen
import com.xiaoxisi.ui.theme.XiaoxisiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XiaoxisiTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }

                Surface(modifier = Modifier.fillMaxSize()) {
                    Crossfade(targetState = currentScreen) { screen ->
                        when (screen) {
                            Screen.Main -> MainScreen(
                                onNavigateToSettings = { currentScreen = Screen.Settings }
                            )
                            Screen.Settings -> SettingsScreen(
                                onBack = { currentScreen = Screen.Main }
                            )
                        }
                    }
                }
            }
        }
    }
}

private enum class Screen { Main, Settings }
