package com.example.ch11.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch11.data.auth.AuthRepository
import com.example.ch11.util.validateRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) =
        _uiState.update { it.copy(email = value, errorMessage = null) }

    fun onPasswordChange(value: String) =
        _uiState.update { it.copy(password = value, errorMessage = null) }

    fun onConfirmPasswordChange(value: String) =
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }

    // Registrasi berhasil = langsung login, sehingga navigasi juga mengikuti authState
    fun register() {
        // TODO [T2.3] Sama seperti LoginViewModel.login():
        //   1. abaikan jika isLoading;
        //   2. validateRegistration(email, password, confirmPassword) -> errorMessage + return;
        //   3. viewModelScope.launch { isLoading = true -> authRepository.register(email.trim(), password)
        //      -> isLoading = false, errorMessage = result.exceptionOrNull()?.message }
    }
}
