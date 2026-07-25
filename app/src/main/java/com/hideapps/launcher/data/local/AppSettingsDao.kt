package com.hideapps.launcher.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 0 LIMIT 1")
    suspend fun getAppSettings(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAppSettings(settings: AppSettingsEntity)

    @Query("DELETE FROM app_settings")
    suspend fun clearSettings()
}
