package com.hideapps.launcher.domain.model

data class AppSettings(
    val pinHash: String?,
    val isAppLocked: Boolean
) {
    val isPinSetup: Boolean
        get() = !pinHash.isNullOrBlank()
}
