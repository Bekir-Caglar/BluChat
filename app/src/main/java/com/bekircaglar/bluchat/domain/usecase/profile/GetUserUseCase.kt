package com.bekircaglar.bluchat.domain.usecase.profile

import com.bekircaglar.bluchat.domain.repository.ChatsRepository
import com.bekircaglar.bluchat.domain.repository.ProfileRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val profileRepository: ProfileRepository,private val chatsRepository: ChatsRepository){

    suspend operator fun invoke() = profileRepository.getUserProfile()

    suspend fun getUserData(user: String) = chatsRepository.getUserData(user)
}