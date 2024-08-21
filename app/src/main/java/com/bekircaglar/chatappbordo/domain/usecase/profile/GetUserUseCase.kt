package com.bekircaglar.chatappbordo.domain.usecase.profile

import com.bekircaglar.chatappbordo.domain.repository.ProfileRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val profileRepository: ProfileRepository){

    suspend operator fun invoke() = profileRepository.getUserProfile()
}