package com.hideapps.launcher.domain.model

import android.graphics.drawable.Drawable

/**
 * Data model representing an installed application.
 */
data class AppInfo(
    val packageName: String,
    val label: String,
    val icon: Drawable? = null,
    val isSystemApp: Boolean = false
)
