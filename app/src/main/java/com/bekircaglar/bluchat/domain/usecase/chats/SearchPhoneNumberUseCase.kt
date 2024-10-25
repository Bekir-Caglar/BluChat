package com.bekircaglar.bluchat.domain.usecase.chats

import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.repository.ChatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchPhoneNumberUseCase @Inject constructor(private val chatsRepository: ChatsRepository) {
    suspend operator fun invoke(query: String): Flow<Response<List<Users>>> {
        return chatsRepository.searchContacts(query)
    }


}