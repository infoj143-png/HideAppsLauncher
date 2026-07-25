package com.hideapps.launcher.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hideapps.launcher.domain.usecase.IsPinSetupUseCase
import com.hideapps.launcher.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isPinSetupUseCase: IsPinSetupUseCase
) : ViewModel() {

    private val _navigationDestination = MutableStateFlow<String?>(null)
    val navigationDestination: StateFlow<String?> = _navigationDestination.asStateFlow()

    init {
        determineNextScreen()
    }

    private fun determineNextScreen() {
        viewModelScope.launch {
            // Splash delay
            delay(1500)
            val isSetup = isPinSetupUseCase()
            if (isSetup) {
                _navigationDestination.value = Screen.PinLogin.route
            } else {
                _navigationDestination.value = Screen.PinSetup.route
            }
        }
    }
}
