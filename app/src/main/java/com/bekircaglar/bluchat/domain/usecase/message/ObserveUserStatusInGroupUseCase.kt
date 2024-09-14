package com.bekircaglar.bluchat.domain.usecase.message

import com.bekircaglar.bluchat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserStatusInGroupUseCase @Inject constructor(private val messageRepository: MessageRepository) {

    operator fun invoke(groupId: String, userId: String): Flow<Boolean> {
        return messageRepository.observeUserStatusInGroup(groupId, userId)
    }
}