package com.hideapps.launcher.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AppSettingsEntity::class, HiddenAppEntity::class], version = 2, exportSchema = false)
abstract class LauncherDatabase : RoomDatabase() {
    abstract val appSettingsDao: AppSettingsDao
    abstract val hiddenAppsDao: HiddenAppsDao

    companion object {
        const val DATABASE_NAME = "hideapps_launcher_db"
    }
}
