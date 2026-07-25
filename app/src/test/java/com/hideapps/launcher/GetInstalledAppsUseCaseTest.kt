package com.hideapps.launcher

import com.hideapps.launcher.domain.model.AppInfo
import com.hideapps.launcher.domain.repository.AppsRepository
import com.hideapps.launcher.domain.usecase.GetInstalledAppsUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetInstalledAppsUseCaseTest {

    private lateinit var fakeAppsRepository: FakeAppsRepository
    private lateinit var getInstalledAppsUseCase: GetInstalledAppsUseCase

    @Before
    fun setUp() {
        fakeAppsRepository = FakeAppsRepository()
        getInstalledAppsUseCase = GetInstalledAppsUseCase(fakeAppsRepository)
    }

    @Test
    fun `test alphabetical sorting and default system exclusion`() = runBlocking {
        // Arrange
        val testApps = listOf(
            AppInfo(packageName = "com.zebra", label = "Zebra App", isSystemApp = false),
            AppInfo(packageName = "com.apple", label = "Apple App", isSystemApp = false),
            AppInfo(packageName = "com.google.android", label = "System App", isSystemApp = true),
            AppInfo(packageName = "com.banana", label = "banana App", isSystemApp = false)
        )
        fakeAppsRepository.setApps(testApps)

        // Act
        val result = getInstalledAppsUseCase()

        // Assert
        // Should exclude "System App" and sort "Apple App", "banana App", "Zebra App"
        assertEquals(3, result.size)
        assertEquals("Apple App", result[0].label)
        assertEquals("banana App", result[1].label)
        assertEquals("Zebra App", result[2].label)
    }

    @Test
    fun `test include system apps`() = runBlocking {
        // Arrange
        val testApps = listOf(
            AppInfo(packageName = "com.zebra", label = "Zebra App", isSystemApp = false),
            AppInfo(packageName = "com.apple", label = "Apple App", isSystemApp = false),
            AppInfo(packageName = "com.google.android", label = "System App", isSystemApp = true),
            AppInfo(packageName = "com.banana", label = "banana App", isSystemApp = false)
        )
        fakeAppsRepository.setApps(testApps)

        // Act
        val result = getInstalledAppsUseCase(excludeSystem = false)

        // Assert
        // Should include all 4 and sort alphabetically: "Apple App", "banana App", "System App", "Zebra App"
        assertEquals(4, result.size)
        assertEquals("Apple App", result[0].label)
        assertEquals("banana App", result[1].label)
        assertEquals("System App", result[2].label)
        assertEquals("Zebra App", result[3].label)
    }
}

class FakeAppsRepository : AppsRepository {
    private var appsList: List<AppInfo> = emptyList()

    fun setApps(apps: List<AppInfo>) {
        this.appsList = apps
    }

    override suspend fun getInstalledApps(): List<AppInfo> {
        return appsList
    }
}
