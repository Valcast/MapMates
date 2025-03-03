package com.example.socialmeetingapp.presentation.common

import android.content.Context
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import com.example.socialmeetingapp.R
import com.example.socialmeetingapp.domain.model.Result
import com.google.android.libraries.identity.googleid.GetGoogleIdOption


class CredentialManager(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)

    private val googleIdOption = GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false).setServerClientId(context.getString(
        R.string.default_web_client_id)).build()

    suspend fun getCredential(): Result<PasswordCredential> {
         return try {
             val credentialResponse = credentialManager.getCredential(
                 request = GetCredentialRequest(listOf(GetPasswordOption())),
                 context = context,
             )

             Result.Success(credentialResponse.credential as PasswordCredential)
         } catch (e: CreateCredentialCancellationException) {
             Result.Error("")
         } catch (e: Exception) {
             Result.Error(e.message ?: "Unknown error")
         }
    }

    suspend fun getGoogleIdCredential(): Result<CustomCredential> {
        return try {
            val credentialResponse = credentialManager.getCredential(
                request = GetCredentialRequest(listOf(googleIdOption)),
                context = context,
            )

            Result.Success(credentialResponse.credential as CustomCredential)
        } catch (e: CreateCredentialCancellationException) {
            Result.Error("")
        }  catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun saveCredential(username: String, password: String): Result<Unit> {
        return try {
            credentialManager.createCredential(request = CreatePasswordRequest(username, password), context = context)

            Result.Success(Unit)
        } catch (e: CreateCredentialCancellationException) {
            return Result.Error("Registration cancelled")
        } catch (e: Exception) {
            return Result.Error(e.message ?: "Unknown error")
        }
    }
}