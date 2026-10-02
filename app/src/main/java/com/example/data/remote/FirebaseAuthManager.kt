package com.example.data.remote

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.model.StaffRole
import com.example.data.model.StaffUser
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseAuthManager(private val context: Context) {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val credentialManager by lazy { CredentialManager.create(context) }

    private val _currentUser = MutableStateFlow<StaffUser?>(null)
    val currentUser: StateFlow<StaffUser?> = _currentUser.asStateFlow()

    init {
        val fbUser = auth.currentUser
        if (fbUser != null) {
            _currentUser.value = StaffUser(
                uid = fbUser.uid,
                name = fbUser.displayName ?: "Zayka Admin",
                email = fbUser.email ?: "admin@zaykachicken.com",
                role = StaffRole.OWNER,
                photoUrl = fbUser.photoUrl?.toString()
            )
        }
    }

    suspend fun signInWithGoogle(webClientId: String): Result<StaffUser> = withContext(Dispatchers.IO) {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response: GetCredentialResponse = credentialManager.getCredential(context, request)
            val credential = response.credential

            if (credential is androidx.credentials.CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val user = authResult.user

                val staffUser = StaffUser(
                    uid = user?.uid ?: "",
                    name = user?.displayName ?: googleIdTokenCredential.displayName ?: "Zayka Admin",
                    email = user?.email ?: googleIdTokenCredential.id,
                    role = StaffRole.OWNER,
                    photoUrl = user?.photoUrl?.toString()
                )
                _currentUser.value = staffUser
                Result.success(staffUser)
            } else {
                Result.failure(Exception("Unsupported credential type"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun switchStaffRole(newRole: StaffRole) {
        val current = _currentUser.value ?: StaffUser(uid = "admin", name = "Staff User", email = "staff@zayka.com")
        _currentUser.value = current.copy(role = newRole)
    }

    fun signOut() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            // Ignore
        }
        _currentUser.value = null
    }

    fun loginWithPin(pin: String, role: StaffRole): Boolean {
        if (pin.length == 4) {
            _currentUser.value = StaffUser(
                uid = "staff-${role.name.lowercase()}",
                name = when (role) {
                    StaffRole.OWNER -> "Yash (Owner)"
                    StaffRole.KITCHEN_MANAGER -> "Master Chef Irfan"
                    StaffRole.DISPATCHER -> "Dispatch Desk (Suresh)"
                },
                email = "${role.name.lowercase()}@zaykachicken.com",
                role = role
            )
            return true
        }
        return false
    }
}
