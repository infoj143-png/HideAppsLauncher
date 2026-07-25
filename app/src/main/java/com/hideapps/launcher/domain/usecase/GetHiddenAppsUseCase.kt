package com.hideapps.launcher.domain.usecase

import com.hideapps.launcher.domain.repository.HiddenAppsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHiddenAppsUseCase @Inject constructor(
    private val repository: HiddenAppsRepository
) {
    operator fun invoke(): Flow<List<String>> {
        return repository.observeHiddenApps()
    }
}
