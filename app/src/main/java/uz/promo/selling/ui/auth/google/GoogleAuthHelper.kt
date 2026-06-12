package uz.promo.selling.ui.auth.google

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

object GoogleAuthHelper {

    // Web client ID from google-services.json (client_type: 3)
    private const val WEB_CLIENT_ID = "222346931020-0r6ocsnjkj9hfhm7ocaca7du7q6glph5.apps.googleusercontent.com"

    /**
     * Explicit "Sign in with Google" button flow. GetSignInWithGoogleOption
     * always shows the full account-picker UI; the previously used
     * GetGoogleIdOption is the silent bottom-sheet flow and throws
     * NoCredentialException ("No credentials available") in many normal
     * situations. Falls back to GetGoogleIdOption only if the button flow
     * itself reports no credentials.
     */
    suspend fun signIn(context: Context): Result<String> {
        val credentialManager = CredentialManager.create(context)

        val buttonFlow = GetCredentialRequest.Builder()
            .addCredentialOption(GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID).build())
            .build()

        return try {
            Result.success(extractIdToken(credentialManager.getCredential(context, buttonFlow)))
        } catch (e: GetCredentialCancellationException) {
            // User dismissed the picker — not an error worth showing.
            Result.failure(Exception(""))
        } catch (e: NoCredentialException) {
            // Fall back to the bottom-sheet flow once before giving up.
            try {
                val sheetFlow = GetCredentialRequest.Builder()
                    .addCredentialOption(
                        GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(WEB_CLIENT_ID)
                            .build()
                    )
                    .build()
                Result.success(extractIdToken(credentialManager.getCredential(context, sheetFlow)))
            } catch (e2: GetCredentialCancellationException) {
                Result.failure(Exception(""))
            } catch (e2: Exception) {
                Result.failure(Exception("${e2.javaClass.simpleName}: ${e2.message}"))
            }
        } catch (e: Exception) {
            // Include the exception type: "NoCredentialException" usually means
            // the Android OAuth client (package name + signing SHA-1) is missing
            // in the Google Cloud console for this build's certificate.
            Result.failure(Exception("${e.javaClass.simpleName}: ${e.message}"))
        }
    }

    private fun extractIdToken(result: GetCredentialResponse): String {
        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return GoogleIdTokenCredential.createFrom(credential.data).idToken
        }
        throw Exception("Unexpected credential type")
    }
}
