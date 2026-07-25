package com.hideapps.launcher.domain.usecase

import com.hideapps.launcher.domain.repository.HiddenAppsRepository
import javax.inject.Inject

class HideAppUseCase @Inject constructor(
    private val repository: HiddenAppsRepository
) {
    suspend operator fun invoke(packageName: String) {
        repository.hideApp(packageName)
    }
}
