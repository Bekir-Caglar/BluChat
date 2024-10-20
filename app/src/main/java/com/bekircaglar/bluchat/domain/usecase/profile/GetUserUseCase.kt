package com.bekircaglar.bluchat.domain.usecase.profile

import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.repository.ChatsRepository
import com.bekircaglar.bluchat.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val profileRepository: ProfileRepository,private val chatsRepository: ChatsRepository){

    suspend operator fun invoke():Flow<Response<Users?>> = profileRepository.getUserProfile()

    suspend fun getUserData(user: String):Flow<Response<Users>> = chatsRepository.getUserData(user)
}