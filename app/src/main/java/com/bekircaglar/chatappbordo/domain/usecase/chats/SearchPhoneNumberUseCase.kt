package com.bekircaglar.chatappbordo.domain.usecase.chats

import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.Users
import com.bekircaglar.chatappbordo.domain.repository.ChatsRepository
import javax.inject.Inject

class SearchPhoneNumberUseCase @Inject constructor(private val chatsRepository: ChatsRepository) {
    suspend operator fun invoke(query: String): Response<List<Users>> {
        return chatsRepository.searchContacts(query)
    }


}