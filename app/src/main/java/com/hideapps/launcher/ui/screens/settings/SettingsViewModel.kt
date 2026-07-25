package com.hideapps.launcher.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hideapps.launcher.domain.usecase.ClearPinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val clearPinUseCase: ClearPinUseCase
) : ViewModel() {

    fun resetPin(onResetComplete: () -> Unit) {
        viewModelScope.launch {
            clearPinUseCase()
            onResetComplete()
        }
    }
}
