package com.hideapps.launcher.data.repository

import com.hideapps.launcher.data.local.SecurePreferencesManager
import com.hideapps.launcher.domain.model.AppSettings
import com.hideapps.launcher.domain.repository.LauncherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LauncherRepositoryImpl @Inject constructor(
    private val securePreferencesManager: SecurePreferencesManager
) : LauncherRepository {

    override suspend fun getAppSettings(): AppSettings = withContext(Dispatchers.IO) {
        val pin = securePreferencesManager.getPin()
        AppSettings(
            pinHash = pin,
            isAppLocked = pin != null
        )
    }

    override suspend fun savePin(pin: String) = withContext(Dispatchers.IO) {
        securePreferencesManager.savePin(pin)
    }

    override suspend fun verifyPin(pin: String): Boolean = withContext(Dispatchers.IO) {
        val savedPin = securePreferencesManager.getPin()
        savedPin == pin
    }

    override suspend fun clearPin() = withContext(Dispatchers.IO) {
        securePreferencesManager.clearPin()
    }
}
