package com.bekircaglar.bluchat.domain.usecase

import com.bekircaglar.bluchat.domain.repository.AuthRepository
import javax.inject.Inject

class CheckPhoneNumberUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend fun checkPhoneNumber(
        phoneNumber: String,
    ) = authRepository.checkPhoneNumber(
        phoneNumber = phoneNumber,
    )
}