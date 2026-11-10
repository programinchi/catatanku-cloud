package com.example.ch11.di

import com.example.ch11.data.auth.AuthRepository
import com.example.ch11.data.auth.FirebaseAuthRepository
import com.example.ch11.data.note.FirestoreNoteRepository
import com.example.ch11.data.note.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Satu-satunya tempat yang tahu bahwa implementasinya Firebase
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(impl: FirebaseAuthRepository): AuthRepository

    @Binds
    abstract fun bindNoteRepository(impl: FirestoreNoteRepository): NoteRepository
}
