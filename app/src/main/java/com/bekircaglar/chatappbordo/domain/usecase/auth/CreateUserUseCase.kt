package com.bekircaglar.chatappbordo.domain.usecase.auth

import com.bekircaglar.chatappbordo.domain.repository.AuthRepository
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun createUser(
        name: String,
        surname: String,
        phoneNumber: String,
        email: String,
    ) = authRepository.createUser(
        name = name,
        surname = surname,
        phoneNumber = phoneNumber,
        email = email,
    )

}