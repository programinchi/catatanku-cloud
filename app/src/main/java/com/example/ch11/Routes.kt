package com.example.ch11

const val ARG_NOTE_ID = "noteId"
const val NEW_NOTE_ID = "new"

sealed class Routes(val route: String) {
    // Layar sebelum login
    data object Login : Routes("login")
    data object Register : Routes("register")

    // Layar setelah login
    data object NoteList : Routes("notes")

    // ID Firestore berupa String; "new" -> catatan baru
    data object NoteEdit : Routes("edit/{$ARG_NOTE_ID}") {
        fun createRoute(noteId: String = NEW_NOTE_ID) = "edit/$noteId"
    }
}

// Layar yang hanya boleh dibuka saat BELUM login
val authRoutes = setOf(Routes.Login.route, Routes.Register.route)
