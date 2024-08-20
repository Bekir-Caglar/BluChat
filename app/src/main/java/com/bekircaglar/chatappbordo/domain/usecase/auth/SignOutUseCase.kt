package com.bekircaglar.chatappbordo.domain.usecase.auth

import com.bekircaglar.chatappbordo.domain.repository.AuthRepository
import com.bekircaglar.chatappbordo.domain.repository.ProfileRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val profileRepository: ProfileRepository) {
    suspend operator fun invoke() = profileRepository.signOut()
}