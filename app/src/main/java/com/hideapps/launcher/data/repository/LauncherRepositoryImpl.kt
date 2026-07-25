package com.hideapps.launcher.data.repository

import com.hideapps.launcher.data.local.AppSettingsDao
import com.hideapps.launcher.data.local.AppSettingsEntity
import com.hideapps.launcher.domain.model.AppSettings
import com.hideapps.launcher.domain.repository.LauncherRepository
import java.security.MessageDigest
import javax.inject.Inject

class LauncherRepositoryImpl @Inject constructor(
    private val dao: AppSettingsDao
) : LauncherRepository {

    override suspend fun getAppSettings(): AppSettings {
        val entity = dao.getAppSettings() ?: AppSettingsEntity()
        return AppSettings(
            pinHash = entity.pinHash,
            isAppLocked = entity.isAppLocked
        )
    }

    override suspend fun savePin(pin: String) {
        val hash = hashPin(pin)
        val entity = dao.getAppSettings()?.copy(pinHash = hash) ?: AppSettingsEntity(pinHash = hash)
        dao.saveAppSettings(entity)
    }

    override suspend fun verifyPin(pin: String): Boolean {
        val entity = dao.getAppSettings() ?: return false
        val hash = hashPin(pin)
        return entity.pinHash == hash
    }

    override suspend fun clearPin() {
        val entity = dao.getAppSettings()?.copy(pinHash = null) ?: AppSettingsEntity(pinHash = null)
        dao.saveAppSettings(entity)
    }

    private fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(pin.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
