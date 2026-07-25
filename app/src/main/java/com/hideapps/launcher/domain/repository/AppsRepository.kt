package com.hideapps.launcher.domain.repository

import com.hideapps.launcher.domain.model.AppInfo

/**
 * Repository interface for managing installed applications.
 */
interface AppsRepository {
    /**
     * Retrieves all installed launchable applications using Android PackageManager.
     */
    suspend fun getInstalledApps(): List<AppInfo>
}
