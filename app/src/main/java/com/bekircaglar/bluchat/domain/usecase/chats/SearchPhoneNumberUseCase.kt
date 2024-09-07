package com.bekircaglar.bluchat.domain.usecase.chats

import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.repository.ChatsRepository
import javax.inject.Inject

class SearchPhoneNumberUseCase @Inject constructor(private val chatsRepository: ChatsRepository) {
    suspend operator fun invoke(query: String): Response<List<Users>> {
        return chatsRepository.searchContacts(query)
    }


}