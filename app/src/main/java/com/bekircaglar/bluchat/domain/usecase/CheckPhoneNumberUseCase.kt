package com.bekircaglar.bluchat.domain.usecase

import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckPhoneNumberUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(phoneNumber: String): Flow<Response<String>> = authRepository.checkPhoneNumber(phoneNumber)
}