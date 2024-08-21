package com.bekircaglar.chatappbordo.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.Users
import com.bekircaglar.chatappbordo.domain.usecase.ExceptionHandlerUseCase
import com.bekircaglar.chatappbordo.domain.usecase.auth.SignOutUseCase
import com.bekircaglar.chatappbordo.domain.usecase.profile.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val exceptionHandlerUseCase: ExceptionHandlerUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {
    private val _users = MutableStateFlow<Users?>(null)
    val users: StateFlow<Users?> get() = _users


    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error
    init {
        getUserProfile()

    }


    fun getUserProfile() = viewModelScope.launch {
        getUserUseCase.invoke().collect {
            _users.value = it
        }

    }




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