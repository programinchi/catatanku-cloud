package com.example.ch12.ui.profile

import android.Manifest
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
import com.example.ch12.ui.camera.hasCameraPermission
import com.example.ch12.ui.camera.openAppSettings
import com.example.ch12.ui.camera.shouldShowCameraRationale
import com.example.ch12.util.PermissionAction
import com.example.ch12.util.cameraClickAction
import com.example.ch12.util.permissionResultAction

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

    val requestCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->

        when (
            permissionResultAction(
                isGranted = isGranted,
                shouldShowRationale = context.shouldShowCameraRationale()
            )
        ) {
            PermissionAction.OpenCamera -> {
                onOpenCamera()
            }

            PermissionAction.ShowDenied -> {
                viewModel.onCameraPermissionDenied()
            }

            PermissionAction.OpenSettings -> {
                dialog = PermissionDialog.Settings
            }

            else -> Unit
        }
    }

    val requestCameraPermission: () -> Unit = {
        requestCamera.launch(Manifest.permission.CAMERA)
    }

    ProfileScreen(
        uiState = uiState,
        hasCamera = hasCamera,
        permissionDialog = dialog,
        onNavigateBack = onNavigateBack,
        onPickFromGallery = launchPicker,

        onTakePhoto = {
            when (
                cameraClickAction(
                    isGranted = context.hasCameraPermission(),
                    shouldShowRationale = context.shouldShowCameraRationale()
                )
            ) {
                PermissionAction.OpenCamera -> {
                    onOpenCamera()
                }

                PermissionAction.ShowRationale -> {
                    dialog = PermissionDialog.Rationale
                }

                PermissionAction.RequestPermission -> {
                    requestCameraPermission()
                }

                else -> Unit
            }
        },

        onRationaleAccepted = {
            dialog = null
            requestCameraPermission()
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