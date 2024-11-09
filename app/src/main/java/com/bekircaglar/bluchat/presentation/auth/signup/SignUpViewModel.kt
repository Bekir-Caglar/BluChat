package com.bekircaglar.bluchat.presentation.auth.signup

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.bekircaglar.bluchat.BuildConfig
import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.utils.UiState
import com.bekircaglar.bluchat.domain.usecase.CheckPhoneNumberUseCase
import com.bekircaglar.bluchat.domain.usecase.ExceptionHandlerUseCase
import com.bekircaglar.bluchat.domain.usecase.auth.AuthUseCase
import com.bekircaglar.bluchat.domain.usecase.auth.CreateUserUseCase
import com.bekircaglar.bluchat.navigation.Screens
import com.google.firebase.auth.FirebaseAuth
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val auth:FirebaseAuth,
    private val createUserUseCase: CreateUserUseCase,
    private val checkPhoneNumberUseCase: CheckPhoneNumberUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    val currentUser = auth.currentUser
    fun checkPassword(
        password: String,
        email: String,
        name: String,
        surname: String,
        phoneNumber: String,
        navController: NavController,
        context: Context
    ) = viewModelScope.launch {
        _uiState.value = UiState.Loading
        try {
            checkPhoneNumberUseCase(phoneNumber).collect {
                when (it) {
                    is Response.Success -> {
                        signUp(
                            name = name,
                            surname = surname,
                            phoneNumber = phoneNumber,
                            email = email,
                            password = password,
                            navController = navController,
                            context = context,
                        )
                    }

                    is Response.Error -> {
                        val errorMessage = it.message
                        _uiState.value = UiState.Error(errorMessage)
                    }

                    else -> {
                        _uiState.value = UiState.Idle
                    }
                }
            }
        } catch (e: Exception) {
            val errorMessage = e.message
            _uiState.value = UiState.Error(errorMessage)
        }
    }

    private fun signUp(
        name: String,
        surname: String,
        phoneNumber: String,
        context: Context,
        email: String,
        navController: NavController,
        password: String,
    ) = viewModelScope.launch {
        _uiState.value = UiState.Loading
        try {
            when (val result = authUseCase.signUpUseCase(email, password)) {
                is Response.Success -> {
                    createUser(
                        name = name,
                        surname = surname,
                        phoneNumber = phoneNumber,
                        email = email,
                        navController = navController,
                        context = context
                    )
                }

                is Response.Error -> {
                    val errorMessage = result.message
                    _uiState.value = UiState.Error(errorMessage)
                }

                else -> {
                    _uiState.value = UiState.Idle
                }
            }
        } catch (e: Exception) {
            val errorMessage = e.message
            _uiState.value = UiState.Error(errorMessage)
        }
    }

    private fun createUser(
        name: String,
        context: Context,
        surname: String,
        phoneNumber: String,
        email: String,
        navController: NavController
    ) = viewModelScope.launch {
        _uiState.value = UiState.Loading
        try {
            val result = createUserUseCase.createUser(
                name = name,
                surname = surname,
                phoneNumber = phoneNumber,
                email = email,
            )
            when (result) {
                is Response.Success -> {
                    _uiState.value = UiState.Success("User created successfully")
                    navController.navigate(Screens.HomeNav.route)
                }

                is Response.Error -> {
                    val errorMessage = result.message
                    _uiState.value = UiState.Error(errorMessage)
                }

                else -> {
                    val errorMessage = "Unknown Error"
                    _uiState.value = UiState.Error(errorMessage)
                }
            }
        } catch (e: Exception) {
            val errorMessage = e.message
            _uiState.value = UiState.Error(errorMessage)
        }
    }
}