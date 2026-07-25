package com.hideapps.launcher.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 0,
    val pinHash: String? = null,
    val isAppLocked: Boolean = false
)
