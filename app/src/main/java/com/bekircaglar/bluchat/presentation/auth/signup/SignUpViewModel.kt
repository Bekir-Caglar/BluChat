package com.bekircaglar.bluchat.presentation.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.domain.usecase.CheckPhoneNumberUseCase
import com.bekircaglar.bluchat.domain.usecase.ExceptionHandlerUseCase
import com.bekircaglar.bluchat.domain.usecase.auth.AuthUseCase
import com.bekircaglar.bluchat.domain.usecase.auth.CreateUserUseCase
import com.bekircaglar.bluchat.navigation.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val exceptionHandlerUseCase: ExceptionHandlerUseCase,
    private val checkPhoneNumberUseCase: CheckPhoneNumberUseCase,
):ViewModel() {




    fun checkPassword(
        password: String,
        email: String,
        name: String,
        surname: String,
        phoneNumber: String,
        onError: (String) -> Unit,
        navController: NavController,
    ) =
        viewModelScope.launch {
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
                                onError = {
                                    onError(it)
                                }
                            )
                        }

                        is Response.Error -> {
                            onError(
                                exceptionHandlerUseCase.invoke(Exception(it.message))
                            )
                        }

                        else -> {

                        }
                    }
                }
            } catch (e: Exception) {
                onError(exceptionHandlerUseCase.invoke(e))
            }
        }

    private fun signUp(
        name: String,
        surname: String,
        phoneNumber: String,
        email: String,
        navController: NavController,
        password: String,
        onError: (String) -> Unit
    ) =
        viewModelScope.launch {
            try {
                when (val result = authUseCase.signUpUseCase(email, password)) {
                    is Response.Success -> {
                        createUser(
                            name = name,
                            surname = surname,
                            phoneNumber = phoneNumber,
                            email = email,
                            navController = navController,
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

    private fun createUser(
        onError: (String) -> Unit,
        name: String,
        surname: String,
        phoneNumber: String,
        email: String,
        navController: NavController
    ) = viewModelScope.launch {

        try {
            val result = createUserUseCase.createUser(
                name = name,
                surname = surname,
                phoneNumber = phoneNumber,
                email = email,
            )
            when (result) {
                is Response.Success -> {
                    navController.navigate(Screens.HomeNav.route)
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