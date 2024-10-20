package com.bekircaglar.bluchat.domain.usecase.profile

import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(private val profileRepository: ProfileRepository){

    suspend operator fun invoke(user: Users): Response<String> = profileRepository.updateUserProfile(user)
}