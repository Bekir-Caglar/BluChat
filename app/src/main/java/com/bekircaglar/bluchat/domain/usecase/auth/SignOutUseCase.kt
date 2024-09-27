package com.bekircaglar.bluchat.domain.usecase.auth

import android.content.Context
import com.bekircaglar.bluchat.domain.repository.ProfileRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val profileRepository: ProfileRepository) {
    suspend operator fun invoke(context: Context) = profileRepository.signOut(context)
}