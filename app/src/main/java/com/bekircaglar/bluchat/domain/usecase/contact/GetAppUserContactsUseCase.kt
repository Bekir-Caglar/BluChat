package com.bekircaglar.bluchat.domain.usecase.contact

import com.bekircaglar.bluchat.domain.model.Users
import com.bekircaglar.bluchat.domain.repository.ContactsRepository
import com.bekircaglar.bluchat.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppUserContactsUseCase @Inject constructor(private val contactsRepository: ContactsRepository) {

    suspend operator fun invoke(contacts: List<Users>, userId: String): Flow<Response<List<Users>>> {
        return contactsRepository.getAppUserContacts(contacts, userId)
    }

}