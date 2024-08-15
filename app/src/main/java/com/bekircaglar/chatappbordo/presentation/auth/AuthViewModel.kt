package com.bekircaglar.chatappbordo.presentation.auth

import androidx.lifecycle.ViewModel
import com.bekircaglar.chatappbordo.domain.usecase.auth.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authUseCase: AuthUseCase):ViewModel() {

}