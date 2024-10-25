package com.bekircaglar.bluchat.domain.usecase.contact

import com.bekircaglar.bluchat.domain.repository.ContactsRepository
import com.bekircaglar.bluchat.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContactsUseCase @Inject constructor(private val contactsRepository: ContactsRepository) {

    suspend operator fun invoke( userId: String): Flow<Response<List<String?>>> {
        return contactsRepository.getContacts(userId)
    }

}
