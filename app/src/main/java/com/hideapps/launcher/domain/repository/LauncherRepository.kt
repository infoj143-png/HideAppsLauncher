package com.hideapps.launcher.domain.repository

import com.hideapps.launcher.domain.model.AppSettings

interface LauncherRepository {
    suspend fun getAppSettings(): AppSettings
    suspend fun savePin(pin: String)
    suspend fun verifyPin(pin: String): Boolean
    suspend fun clearPin()
}
