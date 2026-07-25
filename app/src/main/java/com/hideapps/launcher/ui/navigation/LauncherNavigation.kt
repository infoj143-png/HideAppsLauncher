package com.hideapps.launcher.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hideapps.launcher.ui.screens.home.HomeScreen
import com.hideapps.launcher.ui.screens.home.AppsViewModel
import com.hideapps.launcher.ui.screens.pin.PinScreen
import com.hideapps.launcher.ui.screens.pin.PinViewModel
import com.hideapps.launcher.ui.screens.settings.SettingsScreen
import com.hideapps.launcher.ui.screens.settings.SettingsViewModel
import com.hideapps.launcher.ui.screens.splash.SplashScreen
import com.hideapps.launcher.ui.screens.splash.SplashViewModel

@Composable
fun LauncherNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(
                viewModel = viewModel,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PinSetup.route) {
            val viewModel: PinViewModel = hiltViewModel()
            PinScreen(
                title = "Setup Launcher PIN",
                viewModel = viewModel,
                onSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.PinSetup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PinLogin.route) {
            val viewModel: PinViewModel = hiltViewModel()
            PinScreen(
                title = "Unlock Launcher",
                viewModel = viewModel,
                onSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.PinLogin.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val viewModel: AppsViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToHiddenAppsPin = {
                    navController.navigate(Screen.HiddenAppsPin.route)
                },
                onNavigateToPinSetup = {
                    navController.navigate(Screen.PinSetup.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                },
                onChangePin = {
                    navController.navigate(Screen.PinSetup.route)
                }
            )
        }

        composable(Screen.HiddenAppsPin.route) {
            val viewModel: PinViewModel = hiltViewModel()
            PinScreen(
                title = "Unlock Hidden Apps",
                viewModel = viewModel,
                onSuccess = {
                    navController.navigate(Screen.HiddenApps.route) {
                        popUpTo(Screen.HiddenAppsPin.route) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.HiddenApps.route) {
            val viewModel: com.hideapps.launcher.ui.screens.home.HiddenAppsViewModel = hiltViewModel()
            com.hideapps.launcher.ui.screens.home.HiddenAppsScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
