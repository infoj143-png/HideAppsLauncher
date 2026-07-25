package com.hideapps.launcher.domain.usecase

import com.hideapps.launcher.domain.repository.LauncherRepository
import javax.inject.Inject

class ClearPinUseCase @Inject constructor(
    private val repository: LauncherRepository
) {
    suspend operator fun invoke() {
        repository.clearPin()
    }
}
