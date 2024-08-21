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


    fun checkPassword(
        password: String,
        email: String,
        name: String,
        phoneNumber: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) =
        viewModelScope.launch {
            try {

                when (val result = createUserUseCase.checkPassword(phoneNumber)) {
                    is Response.Success -> {
                        onSuccess()
                        signUp(
                            name = name,
                            phoneNumber = phoneNumber,
                            email = email,
                            password = password,
                            onSuccess = {
                                onSuccess()
                            },
                            onError = {
                                onError(it)
                            }
                        )
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

            try {
                when (val result = authUseCase.signUpUseCase.invoke(email, password)) {
                    is Response.Success -> {
                        onSuccess()
                        createUser(
                            name = name,
                            phoneNumber = phoneNumber,
                            email = email,
                            onSuccess = {
                                onSuccess()
                            },
                            onError = {
                                onError(it)
                            })
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