package com.hideapps.launcher

import com.hideapps.launcher.domain.repository.HiddenAppsRepository
import com.hideapps.launcher.domain.usecase.GetHiddenAppsUseCase
import com.hideapps.launcher.domain.usecase.HideAppUseCase
import com.hideapps.launcher.domain.usecase.UnhideAppUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HiddenAppsUseCaseTest {

    private lateinit var fakeRepository: FakeHiddenAppsRepository
    private lateinit var hideAppUseCase: HideAppUseCase
    private lateinit var unhideAppUseCase: UnhideAppUseCase
    private lateinit var getHiddenAppsUseCase: GetHiddenAppsUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeHiddenAppsRepository()
        hideAppUseCase = HideAppUseCase(fakeRepository)
        unhideAppUseCase = UnhideAppUseCase(fakeRepository)
        getHiddenAppsUseCase = GetHiddenAppsUseCase(fakeRepository)
    }

    @Test
    fun `test hide app successfully`() = runBlocking {
        hideAppUseCase("com.test.app")
        val hiddenApps = getHiddenAppsUseCase().first()
        assertEquals(1, hiddenApps.size)
        assertEquals("com.test.app", hiddenApps[0])
    }

    @Test
    fun `test unhide app successfully`() = runBlocking {
        hideAppUseCase("com.test.app")
        unhideAppUseCase("com.test.app")
        val hiddenApps = getHiddenAppsUseCase().first()
        assertTrue(hiddenApps.isEmpty())
    }
}

class FakeHiddenAppsRepository : HiddenAppsRepository {
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
