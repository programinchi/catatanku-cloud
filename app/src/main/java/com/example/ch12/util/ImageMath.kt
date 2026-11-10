package com.example.ch12.util

import java.util.Locale

const val PROFILE_PHOTO_SIZE = 400
const val PROFILE_PHOTO_QUALITY = 80

fun decodedSizeBytes(
    width: Int,
    height: Int
): Long {
    return width.toLong() * height * 4
}

fun calculateInSampleSize(
    width: Int,
    height: Int,
    reqSize: Int
): Int {
    require(width > 0 && height > 0 && reqSize > 0)

    var inSampleSize = 1
    val shortestSide = minOf(width, height)

    while (
        shortestSide / (inSampleSize * 2) >= reqSize
    ) {
        inSampleSize *= 2
    }

    return inSampleSize
}

data class SquareCrop(
    val left: Int,
    val top: Int,
    val size: Int
)

fun centerSquare(
    width: Int,
    height: Int
): SquareCrop {
    val size = minOf(width, height)

    return SquareCrop(
        left = (width - size) / 2,
        top = (height - size) / 2,
        size = size
    )
}

fun formatBytes(bytes: Long): String = when {
    bytes < 1024 -> "$bytes B"

    bytes < 1024 * 1024 ->
        "${bytes / 1024} KB"

    else -> String.format(
        Locale("id", "ID"),
        "%.1f MB",
        bytes / (1024.0 * 1024.0)
    )
}