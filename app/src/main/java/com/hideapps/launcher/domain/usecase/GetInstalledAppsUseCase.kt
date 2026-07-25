package com.hideapps.launcher.domain.usecase

import com.hideapps.launcher.domain.model.AppInfo
import com.hideapps.launcher.domain.repository.AppsRepository
import javax.inject.Inject

/**
 * Use case to retrieve installed launchable applications, filtered and sorted.
 */
class GetInstalledAppsUseCase @Inject constructor(
    private val repository: AppsRepository
) {
    /**
     * Executes the use case.
     *
     * @param excludeSystem Whether to exclude system apps from the returned list.
     * @return A sorted list of [AppInfo] representing launchable applications.
     */
    suspend operator fun invoke(excludeSystem: Boolean = true): List<AppInfo> {
        val apps = repository.getInstalledApps()
        val filtered = if (excludeSystem) {
            apps.filter { !it.isSystemApp }
        } else {
            apps
        }
        return filtered.sortedBy { it.label.lowercase() }
    }
}
