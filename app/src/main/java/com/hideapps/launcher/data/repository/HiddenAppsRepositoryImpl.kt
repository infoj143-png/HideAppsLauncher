package com.hideapps.launcher.data.repository

import com.hideapps.launcher.data.local.HiddenAppEntity
import com.hideapps.launcher.data.local.HiddenAppsDao
import com.hideapps.launcher.domain.repository.HiddenAppsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HiddenAppsRepositoryImpl @Inject constructor(
    private val dao: HiddenAppsDao
) : HiddenAppsRepository {

    override fun observeHiddenApps(): Flow<List<String>> {
        return dao.observeHiddenApps().map { entities ->
            entities.map { it.packageName }
        }
    }

    override suspend fun getHiddenApps(): List<String> {
        return dao.getHiddenApps().map { it.packageName }
    }

    override suspend fun hideApp(packageName: String) {
        dao.insertHiddenApp(HiddenAppEntity(packageName))
    }

    override suspend fun unhideApp(packageName: String) {
        dao.deleteHiddenApp(packageName)
    }
}
