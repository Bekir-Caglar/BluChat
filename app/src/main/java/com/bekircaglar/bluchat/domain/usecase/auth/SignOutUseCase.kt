package com.bekircaglar.bluchat.domain.usecase.auth

import com.bekircaglar.bluchat.domain.repository.ProfileRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val profileRepository: ProfileRepository) {
    suspend operator fun invoke() = profileRepository.signOut()
}