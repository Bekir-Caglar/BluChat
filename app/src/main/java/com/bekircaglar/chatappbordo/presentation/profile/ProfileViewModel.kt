package com.bekircaglar.chatappbordo.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.usecase.ExceptionHandlerUseCase
import com.bekircaglar.chatappbordo.domain.usecase.auth.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val exceptionHandlerUseCase: ExceptionHandlerUseCase
) : ViewModel() {


    fun signOut(onSuccess: (String) -> Unit, onError: (String) -> Unit) = viewModelScope.launch {
        val result = signOutUseCase.invoke()
        when (result) {
            is Response.Success -> {
                onSuccess("Successfully SignOut")
            }
            is Response.Error -> {
                onError(
                    exceptionHandlerUseCase.invoke(Exception(result.message))
                )
            }
            else -> {
                onError("Unknown Error")
            }
        }
    }


}