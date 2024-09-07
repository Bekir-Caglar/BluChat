package com.bekircaglar.bluchat.domain.usecase.auth

import com.bekircaglar.bluchat.domain.repository.AuthRepository
import javax.inject.Inject

class IsUserAuthenticatedInFirebase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke() = authRepository.isUserAuthenticatedInFirebase()

}