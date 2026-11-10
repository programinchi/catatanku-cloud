
package com.example.ch11.ui.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

suspend fun requestGoogleIdToken(
    context: Context,
    webClientId: String
): String {

    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId(webClientId)
        .setFilterByAuthorizedAccounts(false)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    val credential = CredentialManager
        .create(context)
        .getCredential(
            context = context,
            request = request
        )
        .credential

    if (
        credential is CustomCredential &&
        credential.type ==
        GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        return GoogleIdTokenCredential
            .createFrom(credential.data)
            .idToken
    }

    error("Kredensial Google tidak dikenali.")
}

fun googleSignInErrorMessage(
    e: Exception
): String? = when (e) {

    is GetCredentialCancellationException -> null

    is NoCredentialException ->
        "Tidak ada akun Google di perangkat ini."

    else ->
        e.message ?: "Masuk dengan Google gagal."
}

fun findWebClientId(context: Context): String? {
    @Suppress("DiscouragedApi")
    val id = context.resources.getIdentifier(
        "default_web_client_id",
        "string",
        context.packageName
    )

    return if (id == 0) {
        null
    } else {
        context.getString(id)
    }
}
