package com.hideapps.launcher

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.hideapps.launcher.domain.model.AppInfo
import com.hideapps.launcher.ui.screens.home.AppsViewModel
import com.hideapps.launcher.ui.screens.home.HomeScreen
import com.hideapps.launcher.domain.repository.AppsRepository
import com.hideapps.launcher.domain.repository.HiddenAppsRepository
import com.hideapps.launcher.domain.repository.LauncherRepository
import com.hideapps.launcher.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class HideUnhideUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAppItemShowsConfirmationDialogOnQuickHide() {
        val fakeRepo = FakeAppsRepositoryUi()
        fakeRepo.setApps(listOf(AppInfo(packageName = "com.test.app", label = "Test App", isSystemApp = false)))

        val getInstalledAppsUseCase = com.hideapps.launcher.domain.usecase.GetInstalledAppsUseCase(fakeRepo)

        val fakeHiddenRepo = FakeHiddenAppsRepositoryUi()
        val getHiddenAppsUseCase = com.hideapps.launcher.domain.usecase.GetHiddenAppsUseCase(fakeHiddenRepo)
        val hideAppUseCase = com.hideapps.launcher.domain.usecase.HideAppUseCase(fakeHiddenRepo)

        val fakeLauncherRepo = FakeLauncherRepositoryUi()
        val isPinSetupUseCase = com.hideapps.launcher.domain.usecase.IsPinSetupUseCase(fakeLauncherRepo)

        val viewModel = AppsViewModel(
            getInstalledAppsUseCase = getInstalledAppsUseCase,
            getHiddenAppsUseCase = getHiddenAppsUseCase,
            hideAppUseCase = hideAppUseCase,
            isPinSetupUseCase = isPinSetupUseCase
        )

        // Render HomeScreen
        composeTestRule.setContent {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToSettings = {},
                onNavigateToHiddenAppsPin = {},
                onNavigateToPinSetup = {}
            )
        }

        // Verify App is displayed
        composeTestRule.onNodeWithText("Test App").assertExists()

        // Tap quick hide icon
        composeTestRule.onNodeWithContentDescription("Hide App Directly").performClick()

        // Verify confirmation dialog shows up
        composeTestRule.onNodeWithText("Hide App?").assertExists()
        composeTestRule.onNodeWithText("Cancel").assertExists()
    }
}

private class FakeAppsRepositoryUi : AppsRepository {
    private var appsList: List<AppInfo> = emptyList()

    fun setApps(apps: List<AppInfo>) {
        this.appsList = apps
    }

    override suspend fun getInstalledApps(): List<AppInfo> {
        return appsList
    }
}

private class FakeHiddenAppsRepositoryUi : HiddenAppsRepository {
    private val hiddenAppsFlow = MutableStateFlow<List<String>>(emptyList())

    override fun observeHiddenApps(): Flow<List<String>> {
        return hiddenAppsFlow
    }

    override suspend fun getHiddenApps(): List<String> {
        return hiddenAppsFlow.value
    }

    override suspend fun hideApp(packageName: String) {
        hiddenAppsFlow.value = hiddenAppsFlow.value + packageName
    }

    override suspend fun unhideApp(packageName: String) {
        hiddenAppsFlow.value = hiddenAppsFlow.value - packageName
    }
}

private class FakeLauncherRepositoryUi : LauncherRepository {
    private var pin: String? = null

    override suspend fun getAppSettings(): AppSettings {
        return AppSettings(pinHash = pin, isAppLocked = pin != null)
    }

    override suspend fun savePin(pin: String) {
        this.pin = pin
    }

    override suspend fun verifyPin(pin: String): Boolean {
        return this.pin == pin
    }

    override suspend fun clearPin() {
        this.pin = null
    }
}
