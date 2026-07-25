package com.hideapps.launcher.domain.repository

import kotlinx.coroutines.flow.Flow

interface HiddenAppsRepository {
    fun observeHiddenApps(): Flow<List<String>>
    suspend fun getHiddenApps(): List<String>
    suspend fun hideApp(packageName: String)
    suspend fun unhideApp(packageName: String)
}
