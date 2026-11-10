package com.example.ch12.util

enum class PermissionAction {
    OpenCamera,
    ShowRationale,
    RequestPermission,
    ShowDenied,
    OpenSettings
}

fun cameraClickAction(
    isGranted: Boolean,
    shouldShowRationale: Boolean
): PermissionAction {
    return when {
        isGranted ->
            PermissionAction.OpenCamera

        shouldShowRationale ->
            PermissionAction.ShowRationale

        else ->
            PermissionAction.RequestPermission
    }
}

fun permissionResultAction(
    isGranted: Boolean,
    shouldShowRationale: Boolean
): PermissionAction {
    return when {
        isGranted ->
            PermissionAction.OpenCamera

        shouldShowRationale ->
            PermissionAction.ShowDenied

        else ->
            PermissionAction.OpenSettings
    }
}