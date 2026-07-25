package com.hideapps.launcher.ui.screens.pin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hideapps.launcher.domain.usecase.IsPinSetupUseCase
import com.hideapps.launcher.domain.usecase.SavePinUseCase
import com.hideapps.launcher.domain.usecase.VerifyPinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PinState(
    val pinInput: String = "",
    val instruction: String = "Enter 4-digit PIN",
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class PinViewModel @Inject constructor(
    private val isPinSetupUseCase: IsPinSetupUseCase,
    private val savePinUseCase: SavePinUseCase,
    private val verifyPinUseCase: VerifyPinUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PinState())
    val state: StateFlow<PinState> = _state.asStateFlow()

    private var firstPinAttempt: String? = null
    private var isSetupMode: Boolean = true

    init {
        checkMode()
    }

    private fun checkMode() {
        viewModelScope.launch {
            isSetupMode = !isPinSetupUseCase()
            _state.value = _state.value.copy(
                instruction = if (isSetupMode) "Create your 4-digit PIN" else "Enter security PIN"
            )
        }
    }

    fun onKeyPress(digit: String) {
        val currentInput = _state.value.pinInput
        if (currentInput.length >= 4) return

        val newInput = currentInput + digit
        _state.value = _state.value.copy(pinInput = newInput, error = null)

        if (newInput.length == 4) {
            handleFullPin(newInput)
        }
    }

    fun onDeleteChar() {
        val currentInput = _state.value.pinInput
        if (currentInput.isNotEmpty()) {
            _state.value = _state.value.copy(
                pinInput = currentInput.dropLast(1),
                error = null
            )
        }
    }

    private fun handleFullPin(pin: String) {
        viewModelScope.launch {
            if (isSetupMode) {
                if (firstPinAttempt == null) {
                    firstPinAttempt = pin
                    _state.value = PinState(
                        pinInput = "",
                        instruction = "Confirm your 4-digit PIN"
                    )
                } else {
                    if (firstPinAttempt == pin) {
                        savePinUseCase(pin)
                        _state.value = _state.value.copy(isSuccess = true)
                    } else {
                        firstPinAttempt = null
                        _state.value = PinState(
                            pinInput = "",
                            instruction = "Create your 4-digit PIN",
                            error = "PINs did not match. Start over."
                        )
                    }
                }
            } else {
                val correct = verifyPinUseCase(pin)
                if (correct) {
                    _state.value = _state.value.copy(isSuccess = true)
                } else {
                    _state.value = _state.value.copy(
                        pinInput = "",
                        error = "Incorrect PIN"
                    )
                }
            }
        }
    }

    fun resetState() {
        firstPinAttempt = null
        _state.value = PinState()
        checkMode()
    }
}
