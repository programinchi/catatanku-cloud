package com.example.ch11.ui.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

// Federated identity: pengguna login di Google, BUKAN di aplikasi ini.
// Aplikasi hanya menerima ID token yang ditandatangani Google.
// context harus Activity, karena Credential Manager menampilkan dialog.
suspend fun requestGoogleIdToken(context: Context, webClientId: String): String {
    // TODO [T5.1] Minta ID token Google lewat Credential Manager:
    //   1. GetCredentialRequest.Builder().addCredentialOption(
    //          GetGoogleIdOption.Builder()
    //              .setServerClientId(webClientId)          // Web client ID, bukan Android
    //              .setFilterByAuthorizedAccounts(false)    // tampilkan semua akun di perangkat
    //              .build()
    //      ).build()
    //   2. CredentialManager.create(context).getCredential(context, request).credential
    //   3. Jika credential is CustomCredential dan
    //      credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    //      -> return GoogleIdTokenCredential.createFrom(credential.data).idToken
    //   4. Selain itu: error("Kredensial Google tidak dikenali.")
    error("Belum diimplementasikan: TODO [T5.1]")
}

// null = pengguna membatalkan; tidak perlu ditampilkan sebagai error
fun googleSignInErrorMessage(e: Exception): String? = when (e) {
    is GetCredentialCancellationException -> null
    is NoCredentialException -> "Tidak ada akun Google di perangkat ini."
    else -> e.message ?: "Masuk dengan Google gagal."
}

// default_web_client_id dibuat plugin google-services dari google-services.json,
// tetapi HANYA jika provider Google sudah diaktifkan sebelum file itu diunduh.
// Dicari saat runtime agar proyek tetap ter-compile tanpa Google Sign-In.
fun findWebClientId(context: Context): String? {
    @Suppress("DiscouragedApi")
    val id = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
    return if (id == 0) null else context.getString(id)
}
