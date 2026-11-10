package com.example.ch11.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NoteListRoute(
    userEmail: String?,
    onAddNote: () -> Unit,
    onEditNote: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: NoteListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NoteListScreen(
        uiState = uiState,
        userEmail = userEmail,
        onQueryChange = viewModel::onQueryChange,
        onTagSelected = viewModel::onTagSelected,
        onAddNote = onAddNote,
        onEditNote = onEditNote,
        onDeleteNote = viewModel::deleteNote,
        onTogglePin = viewModel::togglePin,
        onUndoDelete = viewModel::undoDelete,
        onUndoDismissed = viewModel::onUndoDismissed,
        onLogout = onLogout
    )
}
