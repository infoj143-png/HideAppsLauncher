package com.hideapps.launcher.domain.usecase

import com.hideapps.launcher.domain.repository.LauncherRepository
import javax.inject.Inject

class VerifyPinUseCase @Inject constructor(
    private val repository: LauncherRepository
) {
    suspend operator fun invoke(pin: String): Boolean {
        return repository.verifyPin(pin)
    }
}
