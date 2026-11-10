package com.example.ch12.data.photo

import com.example.ch12.data.auth.AuthRepository
import com.example.ch12.data.note.NotSignedInException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePhotoStorage @Inject constructor(
    private val storage: FirebaseStorage,
    private val authRepository: AuthRepository
) : PhotoStorage {

    override fun uploadProfilePhoto(
        bytes: ByteArray
    ): Flow<UploadEvent> = callbackFlow {

        val uid = authRepository.currentUser?.uid
            ?: throw NotSignedInException()

        val reference = storage.reference.child(
            "users/$uid/profile.jpg"
        )

        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()

        val uploadTask = reference.putBytes(
            bytes,
            metadata
        )

        uploadTask.addOnProgressListener { snapshot ->
            trySend(
                UploadEvent.Progress(
                    bytesTransferred = snapshot.bytesTransferred,
                    totalBytes = snapshot.totalByteCount
                )
            )
        }

        uploadTask.addOnSuccessListener {
            launch {
                try {
                    val downloadUrl = reference
                        .downloadUrl
                        .await()
                        .toString()

                    send(
                        UploadEvent.Done(
                            downloadUrl = downloadUrl
                        )
                    )

                    close()
                } catch (e: Exception) {
                    close(
                        PhotoUploadException(
                            storageErrorMessage(e),
                            e
                        )
                    )
                }
            }
        }

        uploadTask.addOnFailureListener { exception ->
            close(
                PhotoUploadException(
                    storageErrorMessage(exception),
                    exception
                )
            )
        }

        awaitClose {
            if (!uploadTask.isComplete) {
                uploadTask.cancel()
            }
        }
    }
}

private fun storageErrorMessage(e: Exception): String {
    return if (e is StorageException) {
        storageErrorMessageForCode(e.errorCode)
    } else {
        e.message ?: "Upload gagal. Coba lagi."
    }
}