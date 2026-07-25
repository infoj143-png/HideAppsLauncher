package com.hideapps.launcher.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HiddenAppsDao {
    @Query("SELECT * FROM hidden_apps")
    fun observeHiddenApps(): Flow<List<HiddenAppEntity>>

    @Query("SELECT * FROM hidden_apps")
    suspend fun getHiddenApps(): List<HiddenAppEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHiddenApp(hiddenApp: HiddenAppEntity)

    @Query("DELETE FROM hidden_apps WHERE packageName = :packageName")
    suspend fun deleteHiddenApp(packageName: String)
}
