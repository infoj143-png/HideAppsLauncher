package com.hideapps.launcher.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hideapps.launcher.domain.model.AppInfo
import com.hideapps.launcher.domain.usecase.GetHiddenAppsUseCase
import com.hideapps.launcher.domain.usecase.GetInstalledAppsUseCase
import com.hideapps.launcher.domain.usecase.UnhideAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HiddenAppsState(
    val hiddenApps: List<AppInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class HiddenAppsViewModel @Inject constructor(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val getHiddenAppsUseCase: GetHiddenAppsUseCase,
    private val unhideAppUseCase: UnhideAppUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())

    val state: StateFlow<HiddenAppsState> = combine(
        _installedApps,
        getHiddenAppsUseCase(),
        _isLoading,
        _error,
        _searchQuery
    ) { apps, hiddenPkgNames, isLoading, error, query ->
        val hiddenAppsList = apps.filter { hiddenPkgNames.contains(it.packageName) }
        val filteredHiddenApps = if (query.isBlank()) {
            hiddenAppsList
        } else {
            hiddenAppsList.filter { it.label.contains(query, ignoreCase = true) }
        }
        HiddenAppsState(
            hiddenApps = filteredHiddenApps,
            isLoading = isLoading,
            error = error,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HiddenAppsState(isLoading = true)
    )

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val apps = getInstalledAppsUseCase(excludeSystem = true)
                _installedApps.value = apps
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to load hidden apps"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun restoreApp(packageName: String) {
        viewModelScope.launch {
            try {
                unhideAppUseCase(packageName)
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to restore app"
            }
        }
    }
}
