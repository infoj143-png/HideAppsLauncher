package com.hideapps.launcher.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hideapps.launcher.domain.model.AppInfo
import com.hideapps.launcher.domain.usecase.GetInstalledAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppsState(
    val apps: List<AppInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class AppsViewModel @Inject constructor(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())

    // Combine search query and installed apps to filter in real-time
    val state: StateFlow<AppsState> = combine(
        _installedApps,
        _isLoading,
        _error,
        _searchQuery
    ) { apps, isLoading, error, query ->
        val filteredApps = if (query.isBlank()) {
            apps
        } else {
            apps.filter { it.label.contains(query, ignoreCase = true) }
        }
        AppsState(
            apps = filteredApps,
            isLoading = isLoading,
            error = error,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppsState(isLoading = true)
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
                _error.value = e.localizedMessage ?: "Failed to load apps"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
