package com.bekircaglar.bluchat.domain.repository

import com.bekircaglar.bluchat.utils.Response
import kotlinx.coroutines.flow.Flow

interface ContactsRepository  {

    suspend fun getContacts(userId: String): Flow<Response<List<String?>>>

    suspend fun addContact(phoneNumber : String, userId: String): Flow<Response<Boolean>>
}