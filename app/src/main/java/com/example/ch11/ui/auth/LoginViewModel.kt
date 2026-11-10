package com.example.ch11.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch11.data.auth.AuthRepository
import com.example.ch11.data.model.AuthUser
import com.example.ch11.util.validateLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// Tidak ada isSuccess: setelah login berhasil, authState berubah dan
// CatatanKuApp yang memindahkan layar (navigasi = fungsi dari state).
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) =
        _uiState.update { it.copy(email = value, errorMessage = null) }

    fun onPasswordChange(value: String) =
        _uiState.update { it.copy(password = value, errorMessage = null) }

    fun login() {
        // TODO [T2.2] Lengkapi login:
        //   1. Ambil state = _uiState.value; abaikan jika state.isLoading.
        //   2. validateLogin(state.email, state.password) -> jika ada pesan,
        //      simpan ke errorMessage lalu return (JANGAN panggil server).
        //   3. authenticate { authRepository.login(state.email.trim(), state.password) }
    }

    // Tugas 5: token didapat Route dari Credential Manager (butuh Activity)
    fun onGoogleIdToken(idToken: String) {
        // TODO [T5.3] authenticate { authRepository.signInWithGoogle(idToken) }
    }

    // message null = pengguna menutup dialog akun Google; bukan error
    fun onGoogleSignInFailed(message: String?) {
        if (message != null) _uiState.update { it.copy(errorMessage = message) }
    }

    // Sudah disediakan: isLoading -> panggil repository -> isLoading false + pesan error
    private fun authenticate(block: suspend () -> Result<AuthUser>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = block()
            _uiState.update {
                it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message)
            }
        }
    }
}
