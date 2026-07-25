package com.hideapps.launcher.ui.screens.home

import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppItem(
    val label: String,
    val packageName: String
)

data class HomeState(
    val apps: List<AppItem> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    fun loadInstalledApps(packageManager: PackageManager) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)

            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfoList = packageManager.queryIntentActivities(intent, 0)
            val appList = resolveInfoList.map { info ->
                AppItem(
                    label = info.loadLabel(packageManager).toString(),
                    packageName = info.activityInfo.packageName
                )
            }.sortedBy { it.label.lowercase() }

            _state.value = HomeState(apps = appList, isLoading = false)
        }
    }
}
