package com.bekircaglar.chatappbordo.domain.usecase.auth

import com.bekircaglar.chatappbordo.domain.repository.AuthRepository
import com.bekircaglar.chatappbordo.domain.repository.ProfileRepository
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        phoneNumber: String,
        email: String,
    ) = authRepository.createUser(
        name = name,
        phoneNumber = phoneNumber,
        email = email,
    )
}