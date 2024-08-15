package com.bekircaglar.chatappbordo.domain.usecase.auth

import com.bekircaglar.chatappbordo.domain.repository.AuthRepository
import javax.inject.Inject

class IsUserAuthenticatedInFirebase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke() = authRepository.isUserAuthenticatedInFirebase()

}