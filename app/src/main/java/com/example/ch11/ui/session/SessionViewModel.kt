package com.example.ch11.ui.session

import androidx.lifecycle.ViewModel
import com.example.ch11.data.auth.AuthRepository
import com.example.ch11.data.model.AuthUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// Status login adalah state seperti state lainnya (Chapter 4).
// Navigasi di CatatanKuApp menjadi fungsi dari state ini.
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // TODO [T2.4] Turunkan dari authRepository.authState agar berubah saat login/logout:
    //   authRepository.authState.stateIn(
    //       scope = viewModelScope,
    //       started = SharingStarted.Eagerly,
    //       initialValue = authRepository.currentUser   // tersedia segera dari disk
    //   )
    // Versi sementara di bawah hanya membaca nilai SEKALI saat ViewModel dibuat.
    val currentUser: StateFlow<AuthUser?> = MutableStateFlow(authRepository.currentUser).asStateFlow()

    // Cukup logout. Navigasi ke layar login terjadi karena state berubah.
    fun logout() = authRepository.logout()
}
