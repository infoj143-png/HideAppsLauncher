package com.hideapps.launcher.domain.usecase

import com.hideapps.launcher.domain.repository.LauncherRepository
import javax.inject.Inject

class IsPinSetupUseCase @Inject constructor(
    private val repository: LauncherRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.getAppSettings().isPinSetup
    }
}
