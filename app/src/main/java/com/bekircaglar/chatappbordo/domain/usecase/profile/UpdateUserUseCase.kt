package com.bekircaglar.chatappbordo.domain.usecase.profile

import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.Users
import com.bekircaglar.chatappbordo.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(private val profileRepository: ProfileRepository){

    suspend operator fun invoke(user: Users):Response<String> = profileRepository.updateUserProfile(user)
}