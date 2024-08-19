package com.bekircaglar.chatappbordo.presentation.auth

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.ShowErrorDialog
import com.bekircaglar.chatappbordo.domain.usecase.ExceptionHandlerUseCase
import com.bekircaglar.chatappbordo.domain.usecase.auth.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authUseCase: AuthUseCase,private val exceptionHandlerUseCase: ExceptionHandlerUseCase) : ViewModel() {


    fun signIn(email: String, password: String,onSuccess:()->Unit,onError: (String) -> Unit) = viewModelScope.launch {
        try {

            val result = authUseCase.signInUseCase.invoke(email, password)
            when (result) {
                is Response.Success -> {
                    onSuccess()
                }
                is Response.Error -> {
                    exceptionHandlerUseCase.invoke(Exception(result.message))
                }
                else -> {

                }
            }
        } catch (e: Exception) {
                exceptionHandlerUseCase.invoke(e)
        }
    }


    fun signUp(email: String, password: String,onSuccess:()->Unit,onError:(String) ->Unit)= viewModelScope.launch{
        try {
            val result = authUseCase.signUpUseCase.invoke(email, password)
            when (result) {
                is Response.Success -> {
                    onSuccess()
                }
                is Response.Error -> {
                    onError(result.message)
                }
                else -> {
                }
            }
        } catch (e:Exception){
            onError(e.message.toString())
        }
    }


}