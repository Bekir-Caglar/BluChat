package com.bekircaglar.bluchat.presentation.auth.signin

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bekircaglar.bluchat.R
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.UiState
import com.bekircaglar.bluchat.domain.model.Users

import com.bekircaglar.bluchat.domain.usecase.ExceptionHandlerUseCase
import com.bekircaglar.bluchat.domain.usecase.auth.AuthUseCase
import com.bekircaglar.bluchat.domain.usecase.auth.CheckIsUserAlreadyExistUseCase
import com.bekircaglar.bluchat.domain.usecase.auth.CreateUserUseCase
import com.bekircaglar.bluchat.domain.usecase.auth.SignOutUseCase
import com.facebook.AccessToken
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val exceptionHandlerUseCase: ExceptionHandlerUseCase,
    private val auth: FirebaseAuth,
    private val createUserUseCase: CreateUserUseCase,
    private val checkIsUserAlreadyExistUseCase: CheckIsUserAlreadyExistUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private lateinit var googleSignInClient: GoogleSignInClient

    fun signOut(context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) =
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                signOutUseCase(context = context)
                _uiState.value = UiState.Success("Signed out successfully")
                onSuccess()
            } catch (e: Exception) {
                val errorMessage = exceptionHandlerUseCase.invoke(e)
                _uiState.value = UiState.Error(errorMessage)
                onError(errorMessage)
            }
        }

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

    fun handleFacebookSignInResult(
        result: LoginResult,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onPhoneNumberNotExist: () -> Unit
    ) = viewModelScope.launch {
        _uiState.value = UiState.Loading
        val token = result.accessToken.token
        val credential = FacebookAuthProvider.getCredential(token)
        val authResult = Firebase.auth.signInWithCredential(credential).await()

        val user = authResult.user ?: return@launch onError("User is null")

        val email = user.email ?: return@launch onError("Email is null")

        checkIsUserAlreadyExistUseCase(email).collect { response ->
            when (response) {
                is Response.Success -> {
                    if (response.data) {
                        _uiState.value = UiState.Success("User exists")
                        onSuccess()
                    } else {
                        if (user.phoneNumber.isNullOrBlank()) {
                            _uiState.value = UiState.Error("Phone number not exist")
                            onPhoneNumberNotExist()
                        } else {
                            saveUserToDatabase(user, onSuccess, onError)
                            _uiState.value = UiState.Success("User saved to database")
                            onSuccess()
                        }
                    }
                }

                is Response.Error -> {
                    val errorMessage = response.message
                    _uiState.value = UiState.Error(errorMessage)
                    onError(errorMessage)
                }

                is Response.Loading -> {
                    _uiState.value = UiState.Loading
                }

                is Response.Idle -> {
                    _uiState.value = UiState.Idle
                }
            }
        }
    }

    fun handleGoogleSignInResult(
        task: Task<GoogleSignInAccount>,
        onSuccess: () -> Unit,
        onPhoneNumberNotExist: () -> Unit,
        onError: (String) -> Unit,
    ) {
        _uiState.value = UiState.Loading
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken ?: return onError("ID token is null")

            val credential = GoogleAuthProvider.getCredential(idToken, null)
            viewModelScope.launch {
                try {
                    val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
                    val user = authResult.user ?: return@launch onError("User is null")

                    val email = authResult.user?.email ?: return@launch onError("Email is null")
                    checkIsUserAlreadyExistUseCase(email).collect { response ->
                        when (response) {
                            is Response.Success -> {
                                if (response.data) {
                                    _uiState.value = UiState.Success("User exists")
                                    onSuccess()
                                } else {
                                    if (user.phoneNumber.isNullOrBlank()) {
                                        _uiState.value = UiState.Error("Phone number not exist")
                                        onPhoneNumberNotExist()
                                    } else {
                                        saveUserToDatabase(user, onSuccess, onError)
                                        _uiState.value = UiState.Success("User saved to database")
                                        onSuccess()
                                    }
                                }
                            }

                            is Response.Error -> {
                                val errorMessage = response.message
                                _uiState.value = UiState.Error(errorMessage)
                                onError(errorMessage)
                            }

                            is Response.Loading -> {
                                _uiState.value = UiState.Loading
                            }

                            is Response.Idle -> {
                                _uiState.value = UiState.Idle
                            }
                        }
                    }
                } catch (e: CancellationException) {
                    _uiState.value = UiState.Idle
                } catch (e: Exception) {
                    val errorMessage = e.message ?: "Authentication failed"
                    _uiState.value = UiState.Error(errorMessage)
                    onError(errorMessage)
                }
            }
        } catch (e: ApiException) {
            val errorMessage = exceptionHandlerUseCase.invoke(e)
            _uiState.value = UiState.Error(errorMessage)
            onError(errorMessage)
        }
    }

    fun saveCurrentGoogleUserToDatabase(
        onSuccess: () -> Unit, phoneNumber: String, user: GoogleSignInAccount?
    ) {
        val googleUser = Users(
            uid = user?.id ?: "",
            name = user?.givenName ?: "",
            surname = user?.familyName ?: "",
            email = user?.email ?: "",
            profileImageUrl = user?.photoUrl.toString(),
            phoneNumber = phoneNumber
        )
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                createUserUseCase.createUser(
                    name = googleUser.name,
                    surname = googleUser.surname,
                    phoneNumber = googleUser.phoneNumber,
                    email = googleUser.email,
                    userImageUrl = googleUser.profileImageUrl
                )
                _uiState.value = UiState.Success("User saved to database")
                onSuccess()
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Error creating user"
                _uiState.value = UiState.Error(errorMessage)
                Log.e("SaveUser", errorMessage)
            }
        }
    }

    private fun saveUserToDatabase(
        user: FirebaseUser, onSuccess: () -> Unit, onError: (String) -> Unit
    ) {
        val googleUser = Users(
            uid = user.uid,
            name = user.displayName ?: "",
            email = user.email ?: "",
            profileImageUrl = user.photoUrl.toString(),
            phoneNumber = user.phoneNumber ?: "61"
        )

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                createUserUseCase.createUser(
                    name = googleUser.name,
                    surname = " ",
                    phoneNumber = googleUser.phoneNumber,
                    email = googleUser.email,
                    userImageUrl = googleUser.profileImageUrl
                )
                _uiState.value = UiState.Success("User saved to database")
                onSuccess()
            } catch (e: Exception) {
                val errorMessage = e.message ?: "User creation failed"
                _uiState.value = UiState.Error(errorMessage)
                onError(errorMessage)
            }
        }
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) =
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                when (val result = authUseCase.signInUseCase.invoke(email, password)) {
                    is Response.Success -> {
                        _uiState.value = UiState.Success("Signed in successfully")
                        onSuccess()
                    }

                    is Response.Error -> {
                        val errorMessage = exceptionHandlerUseCase.invoke(Exception(result.message))
                        _uiState.value = UiState.Error(errorMessage)
                        onError(errorMessage)
                    }

                    else -> {
                        _uiState.value = UiState.Idle
                    }
                }
            } catch (e: Exception) {
                val errorMessage = exceptionHandlerUseCase.invoke(e)
                _uiState.value = UiState.Error(errorMessage)
                onError(errorMessage)
            }
        }
}