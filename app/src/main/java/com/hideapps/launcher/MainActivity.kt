package com.hideapps.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hideapps.launcher.ui.navigation.LauncherNavigation
import com.hideapps.launcher.ui.theme.HideAppsLauncherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HideAppsLauncherTheme {
                LauncherNavigation()
            }
        }
    }
}
