package com.bekircaglar.bluchat.domain.usecase.contact

import com.bekircaglar.bluchat.domain.repository.ContactsRepository
import com.bekircaglar.bluchat.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddContactUseCase @Inject constructor(
    private val contactRepository: ContactsRepository
) {
    suspend operator fun invoke(phoneNumber: String,userId:String): Flow<Response<Boolean>> {
        return contactRepository.addContact(phoneNumber = phoneNumber, userId =userId)
    }


}