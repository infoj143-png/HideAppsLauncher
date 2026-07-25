package com.hideapps.launcher

import com.hideapps.launcher.domain.model.AppSettings
import com.hideapps.launcher.domain.repository.LauncherRepository
import com.hideapps.launcher.domain.usecase.ClearPinUseCase
import com.hideapps.launcher.domain.usecase.IsPinSetupUseCase
import com.hideapps.launcher.domain.usecase.SavePinUseCase
import com.hideapps.launcher.domain.usecase.VerifyPinUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LauncherUseCaseTest {

    private lateinit var fakeRepository: FakeLauncherRepository
    private lateinit var isPinSetupUseCase: IsPinSetupUseCase
    private lateinit var savePinUseCase: SavePinUseCase
    private lateinit var verifyPinUseCase: VerifyPinUseCase
    private lateinit var clearPinUseCase: ClearPinUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeLauncherRepository()
        isPinSetupUseCase = IsPinSetupUseCase(fakeRepository)
        savePinUseCase = SavePinUseCase(fakeRepository)
        verifyPinUseCase = VerifyPinUseCase(fakeRepository)
        clearPinUseCase = ClearPinUseCase(fakeRepository)
    }

    @Test
    fun `test initial state is not setup`() = runBlocking {
        assertFalse(isPinSetupUseCase())
    }

    @Test
    fun `test pin setup successfully`() = runBlocking {
        val success = savePinUseCase("1234")
        assertTrue(success)
        assertTrue(isPinSetupUseCase())
    }

    @Test
    fun `test pin length validation fails`() = runBlocking {
        val success = savePinUseCase("12")
        assertFalse(success)
        assertFalse(isPinSetupUseCase())
    }

    @Test
    fun `test pin verification correct`() = runBlocking {
        savePinUseCase("9999")
        val isCorrect = verifyPinUseCase("9999")
        assertTrue(isCorrect)
    }

    @Test
    fun `test pin verification incorrect`() = runBlocking {
        savePinUseCase("9999")
        val isCorrect = verifyPinUseCase("1111")
        assertFalse(isCorrect)
    }

    @Test
    fun `test clear pin`() = runBlocking {
        savePinUseCase("4567")
        assertTrue(isPinSetupUseCase())
        clearPinUseCase()
        assertFalse(isPinSetupUseCase())
    }
}

class FakeLauncherRepository : LauncherRepository {
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
