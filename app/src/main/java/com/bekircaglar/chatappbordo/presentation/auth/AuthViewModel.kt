package com.bekircaglar.chatappbordo.presentation.auth

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.ShowErrorDialog
import com.bekircaglar.chatappbordo.domain.usecase.auth.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authUseCase: AuthUseCase) : ViewModel() {


    fun signIn(email: String, password: String,onSucces:()->Unit) = viewModelScope.launch {
        try {
            val result = authUseCase.signInUseCase.invoke(email, password)
            when (result) {
                is Response.Success -> {
                    println("Success")
                    onSucces()
                }
                is Response.Error -> {
                    println("Error")
                }
                else -> {
                    println("Else")
                }
            }
        } catch (e: Exception) {
//            ShowErrorDialog().showErrorDialog(context =, e.message.toString())
            println(e.message)

        }
    }


    fun signUp(email: String, password: String,onSucces:()->Unit)= viewModelScope.launch{
        try {
            val result = authUseCase.signUpUseCase.invoke(email, password)
            when (result) {
                is Response.Success -> {
                    println("Success")
                    onSucces()
                }
                is Response.Error -> {
                    println("Error")
                }
                else -> {
                    println("Else")
                }
            }
        } catch (e:Exception){
            println(e.message)

        }
    }


}