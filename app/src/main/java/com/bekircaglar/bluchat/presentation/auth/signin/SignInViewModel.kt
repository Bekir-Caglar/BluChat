package com.bekircaglar.bluchat.presentation.auth.signin

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.domain.model.Users

import com.bekircaglar.bluchat.domain.usecase.ExceptionHandlerUseCase
import com.bekircaglar.bluchat.domain.usecase.auth.AuthUseCase
import com.bekircaglar.bluchat.domain.usecase.auth.CreateUserUseCase
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val exceptionHandlerUseCase: ExceptionHandlerUseCase,
    private val auth: FirebaseAuth,
    private val createUserUseCase: CreateUserUseCase
) : ViewModel() {
    private lateinit var googleSignInClient: GoogleSignInClient

    fun initGoogleSignInClient(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()

            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }
    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun handleGoogleSignInResult(
        task: Task<GoogleSignInAccount>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->

                val credential = GoogleAuthProvider.getCredential(idToken, null)
                viewModelScope.launch {
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener { authTask ->
                            if (authTask.isSuccessful ) {
                                val user = authTask.result.user
                                if (user?.phoneNumber.isNullOrBlank()){
                                    val googleUser = Users(
                                        uid = user?.uid ?: "",
                                        name = user?.displayName ?: "",
                                        email = user?.email ?: "",
                                        profileImageUrl = user?.photoUrl.toString(),
                                        phoneNumber = if (user?.phoneNumber.isNullOrBlank()) "61"
                                        else user?.phoneNumber.toString()
                                    )
                                    viewModelScope.launch {
                                        createUserUseCase.createUser(
                                            name = googleUser.name,
                                            surname = " ",
                                            phoneNumber = googleUser.phoneNumber,
                                            email = googleUser.email,
                                            userImageUrl = googleUser.profileImageUrl
                                        )
                                    }

                                }


                            onSuccess()
                        }
                            else {
                    onError(authTask.exception?.message ?: "Authentication failed")
                }
                }.await()
            }
        }
    } catch (e: ApiException)
    {
        onError(exceptionHandlerUseCase.invoke(e))
    }
}

fun handleFacebookAccessToken(
    token: AccessToken,
    onSuccess: () -> Unit,
    onError: (Exception) -> Unit
) {
    val credential = FacebookAuthProvider.getCredential(token.token)
    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                task.exception?.let { onError(it) }
            }
        }
}

fun signIn(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) =
    viewModelScope.launch {
        try {

            when (val result = authUseCase.signInUseCase.invoke(email, password)) {
                is Response.Success -> {
                    onSuccess()
                }

                is Response.Error -> {
                    onError(
                        exceptionHandlerUseCase.invoke(Exception(result.message))
                    )
                }

                else -> {

                }
            }
        } catch (e: Exception) {
            onError(exceptionHandlerUseCase.invoke(e))
        }
    }
}