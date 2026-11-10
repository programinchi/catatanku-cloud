package com.example.ch12.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ch12.ui.camera.hasCameraHardware
import com.example.ch12.ui.camera.openAppSettings

enum class PermissionDialog {
    Rationale,
    Settings
}

@Composable
fun ProfileRoute(
    onNavigateBack: () -> Unit,
    onOpenCamera: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val hasCamera = remember { context.hasCameraHardware() }

    var dialog by rememberSaveable {
        mutableStateOf<PermissionDialog?>(null)
    }

    val pickPhoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.onPhotoSelected(uri.toString())
        }
    }

    val launchPicker: () -> Unit = {
        pickPhoto.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    ProfileScreen(
        uiState = uiState,
        hasCamera = hasCamera,
        permissionDialog = dialog,
        onNavigateBack = onNavigateBack,
        onPickFromGallery = launchPicker,
        onTakePhoto = { onOpenCamera() },
        onRationaleAccepted = {
            dialog = null
        },
        onRationaleDeclined = {
            dialog = null
            launchPicker()
        },
        onOpenSettings = {
            dialog = null
            context.openAppSettings()
        },
        onDismissDialog = {
            dialog = null
        },
        onUpload = viewModel::upload,
        onCancelSelection = viewModel::cancelSelection,
        onCancelUpload = viewModel::cancelUpload,
        onMessageShown = viewModel::onMessageShown
    )
}