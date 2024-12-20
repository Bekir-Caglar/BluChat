package com.bekircaglar.bluchat.domain.usecase.auth

import com.bekircaglar.bluchat.domain.repository.AuthRepository
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun createUser(
        name: String,
        surname: String,
        phoneNumber: String,
        email: String,
        userImageUrl:String?= ""
    ) = authRepository.createUser(
        name = name,
        surname = surname,
        phoneNumber = phoneNumber,
        email = email,
        userImageUrl = userImageUrl
    )

}