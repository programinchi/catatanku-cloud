package com.example.ch12.data.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.example.ch12.util.PROFILE_PHOTO_QUALITY
import com.example.ch12.util.PROFILE_PHOTO_SIZE
import com.example.ch12.util.calculateInSampleSize
import com.example.ch12.util.centerSquare
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

class AndroidImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageCompressor {

    override suspend fun compress(
        uri: String
    ): CompressedImage = withContext(Dispatchers.IO) {

        val source = Uri.parse(uri)

        // Step 1: read original image dimensions
        val bounds = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        open(source).use { stream ->
            BitmapFactory.decodeStream(
                stream,
                null,
                bounds
            )
        }

        val originalWidth = bounds.outWidth
        val originalHeight = bounds.outHeight

        if (originalWidth <= 0 || originalHeight <= 0) {
            throw ImageDecodeException(
                "Berkas yang dipilih bukan gambar."
            )
        }

        // Step 2: decode smaller bitmap
        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(
                originalWidth,
                originalHeight,
                PROFILE_PHOTO_SIZE
            )
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }

        val sampled = open(source).use { stream ->
            BitmapFactory.decodeStream(
                stream,
                null,
                options
            )
        } ?: throw ImageDecodeException(
            "Gambar tidak dapat didekode."
        )

        try {
            // Step 3: center square crop
            val crop = centerSquare(
                sampled.width,
                sampled.height
            )

            val scale = minOf(
                1f,
                PROFILE_PHOTO_SIZE.toFloat() / crop.size
            )

            // Step 4: resize and fix EXIF rotation
            val matrix = Matrix().apply {
                postScale(scale, scale)
                postRotate(readRotation(source).toFloat())
            }

            val resultBitmap = Bitmap.createBitmap(
                sampled,
                crop.left,
                crop.top,
                crop.size,
                crop.size,
                matrix,
                true
            )

            try {
                // Step 5: compress as JPEG
                val output = ByteArrayOutputStream()

                val success = resultBitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    PROFILE_PHOTO_QUALITY,
                    output
                )

                if (!success) {
                    throw ImageDecodeException(
                        "Gagal mengompres gambar."
                    )
                }

                CompressedImage(
                    bytes = output.toByteArray(),
                    originalWidth = originalWidth,
                    originalHeight = originalHeight,
                    width = resultBitmap.width,
                    height = resultBitmap.height
                )
            } finally {
                if (resultBitmap !== sampled) {
                    resultBitmap.recycle()
                }
            }
        } finally {
            sampled.recycle()
        }
    }

    private fun open(uri: Uri): InputStream {
        return context.contentResolver.openInputStream(uri)
            ?: throw ImageDecodeException(
                "Gambar tidak dapat dibuka."
            )
    }

    private fun readRotation(uri: Uri): Int {
        return runCatching {
            open(uri).use { stream ->
                ExifInterface(stream).rotationDegrees
            }
        }.getOrDefault(0)
    }
}