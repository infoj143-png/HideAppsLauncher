package com.hideapps.launcher.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object PinSetup : Screen("pin_setup")
    object PinLogin : Screen("pin_login")
    object Home : Screen("home")
    object Settings : Screen("settings")
    object HiddenAppsPin : Screen("hidden_apps_pin")
    object HiddenApps : Screen("hidden_apps")
}
