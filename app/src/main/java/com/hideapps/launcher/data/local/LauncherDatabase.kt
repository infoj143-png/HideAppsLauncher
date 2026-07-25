package com.hideapps.launcher.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AppSettingsEntity::class], version = 1, exportSchema = false)
abstract class LauncherDatabase : RoomDatabase() {
    abstract val appSettingsDao: AppSettingsDao

    companion object {
        const val DATABASE_NAME = "hideapps_launcher_db"
    }
}
