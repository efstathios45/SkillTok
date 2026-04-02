package com.skilltok.app

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)
    private val auth = FirebaseAuth.getInstance()

    suspend fun signIn(activity: Activity): Result<Boolean> {
        val serverClientId = context.getString(R.string.google_server_client_id)
        Log.d("GoogleAuth", "Starting sign in with Client ID: ${serverClientId.take(10)}...")
        
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // Show all accounts
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(activity, request)
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
            val googleToken = googleIdTokenCredential.idToken
            
            val credential = GoogleAuthProvider.getCredential(googleToken, null)
            auth.signInWithCredential(credential).await()
            Log.d("GoogleAuth", "Sign in successful")
            Result.success(true)
        } catch (e: GetCredentialException) {
            Log.e("GoogleAuth", "Credential Manager Error: ${e.type} - ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("GoogleAuth", "Unexpected Error: ${e.message}", e)
            Result.failure(e)
        }
    }
}
