package com.example.ch11.data.model

// Versi ringkas FirebaseUser. ViewModel cukup mengenal class ini,
// sehingga tidak bergantung langsung pada SDK Firebase.
data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String? = null
)
