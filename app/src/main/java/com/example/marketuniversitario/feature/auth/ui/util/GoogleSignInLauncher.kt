package com.example.marketuniversitario.feature.auth.ui.util

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.marketuniversitario.feature.auth.domain.exceptions.AuthException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

class GoogleSignInLauncher {

    suspend fun getIdToken( context: Context ): Result<String> {
        return try {
            val credentialManager =
                CredentialManager.create(context)

            val signInOption =
                GetSignInWithGoogleOption.Builder(
                    SERVER_CLIENT_ID
                ).build()

            val request =
                GetCredentialRequest.Builder()
                    .addCredentialOption(signInOption)
                    .build()

            val result =
                credentialManager.getCredential(
                    context,
                    request
                )

            val googleCredential =
                GoogleIdTokenCredential.createFrom(
                    result.credential.data
                )

            Result.success(googleCredential.idToken)

        } catch (e: GetCredentialCancellationException) {
            Result.failure(AuthException.UserCancelled)

        } catch (e: GetCredentialException) {
            Result.failure(e)

        } catch (e: GoogleIdTokenParsingException) {
            Result.failure(e)
        }
    }

    companion object {
        private const val SERVER_CLIENT_ID =
            "367008890014-ne8sgsoc8jnv7a92e6l8tloiihtt4fms.apps.googleusercontent.com"
    }
}