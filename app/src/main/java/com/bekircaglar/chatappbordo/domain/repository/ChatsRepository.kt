package com.bekircaglar.chatappbordo.domain.repository

import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.Users

interface ChatsRepository {

    suspend fun searchContacts(query: String): Response<List<Users>>


}