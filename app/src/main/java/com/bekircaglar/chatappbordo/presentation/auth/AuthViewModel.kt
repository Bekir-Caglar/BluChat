package com.bekircaglar.chatappbordo.presentation.auth

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.ShowErrorDialog
import com.bekircaglar.chatappbordo.domain.usecase.ExceptionHandlerUseCase
import com.bekircaglar.chatappbordo.domain.usecase.auth.AuthUseCase
import com.bekircaglar.chatappbordo.domain.usecase.auth.CreateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val exceptionHandlerUseCase: ExceptionHandlerUseCase
) : ViewModel() {


    fun signIn(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) =
        viewModelScope.launch {
            try {

                val result = authUseCase.signInUseCase.invoke(email, password)
                when (result) {
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


    fun signUp(
        name: String,
        phoneNumber: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) =
        viewModelScope.launch {
            val result1 = createUserUseCase.checkPassword(
                name = name,
                phoneNumber = phoneNumber,
                email = email,
            )
            if (result1 is Response.Success) {
                try {
                    val result = authUseCase.signUpUseCase.invoke(email, password)
                    when (result) {
                        is Response.Success -> {
                            onSuccess()
                            createUser(
                                onError = {
                                    onError(it)
                                },
                                onSuccess = {
                                },
                                name = name,
                                phoneNumber = phoneNumber,
                                email = email,
                            )
                        }

                        is Response.Error -> {
                            onError(exceptionHandlerUseCase.invoke(Exception(result.message)))
                        }

                        else -> {
                        }
                    }
                } catch (e: Exception) {
                    onError(exceptionHandlerUseCase.invoke(e))
                }

            } else {
                onError("Password is not correct")
            }


        }

    fun createUser(
        onError: (String) -> Unit,
        onSuccess: (String) -> Unit,
        name: String,
        phoneNumber: String,
        email: String,
    ) = viewModelScope.launch {

        try {
            val result = createUserUseCase.createUser(
                name = name,
                phoneNumber = phoneNumber,
                email = email,
            )
            when (result) {
                is Response.Success -> {
                    onSuccess("User Created Successfully")
                }

                is Response.Error -> {
                    onError(exceptionHandlerUseCase.invoke(Exception(result.message)))
                }

                else -> {
                    onError("Unknown Error")

                }
            }
        } catch (e: Exception) {
            onError(exceptionHandlerUseCase.invoke(e))
        }


    }


}