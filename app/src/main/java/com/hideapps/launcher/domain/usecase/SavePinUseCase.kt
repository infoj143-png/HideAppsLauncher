package com.hideapps.launcher.domain.usecase

import com.hideapps.launcher.domain.repository.LauncherRepository
import javax.inject.Inject

class SavePinUseCase @Inject constructor(
    private val repository: LauncherRepository
) {
    suspend operator fun invoke(pin: String): Boolean {
        if (pin.length < 4) return false
        repository.savePin(pin)
        return true
    }
}
