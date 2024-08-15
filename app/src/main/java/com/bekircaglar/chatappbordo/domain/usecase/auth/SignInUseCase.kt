package com.bekircaglar.chatappbordo.domain.usecase.auth

import com.bekircaglar.chatappbordo.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val authRepository: AuthRepository ) {

    suspend operator fun invoke(email: String, password: String) =
        authRepository.signIn(email, password)

}