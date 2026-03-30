package com.skilltok.app

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleAuthManager(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    suspend fun signIn(): GoogleIdTokenCredential? {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.google_server_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            GoogleIdTokenCredential.createFrom(result.credential.data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
