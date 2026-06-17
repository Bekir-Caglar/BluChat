package com.bekircaglar.bluchat.data.repository.local

import com.bekircaglar.bluchat.data.local.dao.ChatRoomDao
import com.bekircaglar.bluchat.data.local.entities.toDomainModel
import com.bekircaglar.bluchat.data.local.entities.toEntity
import com.bekircaglar.bluchat.domain.model.ChatRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalChatRoomRepository @Inject constructor(
    private val chatRoomDao: ChatRoomDao
) {

    suspend fun getChatRoomById(chatId: String): ChatRoom? {
        return chatRoomDao.getChatRoomById(chatId)?.toDomainModel()
    }

    fun getChatRoomByIdFlow(chatId: String): Flow<ChatRoom?> {
        return chatRoomDao.getChatRoomByIdFlow(chatId).map { it?.toDomainModel() }
    }

    suspend fun getChatRoomsForUser(userId: String): List<ChatRoom> {
        return chatRoomDao.getChatRoomsForUser(userId).map { it.toDomainModel() }
    }

    fun getChatRoomsForUserFlow(userId: String): Flow<List<ChatRoom>> {
        return chatRoomDao.getChatRoomsForUserFlow(userId).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    suspend fun getAllChatRooms(): List<ChatRoom> {
        return chatRoomDao.getAllChatRooms().map { it.toDomainModel() }
    }

    suspend fun insertChatRoom(chatRoom: ChatRoom) {
        chatRoomDao.insertChatRoom(chatRoom.toEntity())
    }

    suspend fun insertChatRooms(chatRooms: List<ChatRoom>) {
        chatRoomDao.insertChatRooms(chatRooms.map { it.toEntity() })
    }

    suspend fun updateChatRoom(chatRoom: ChatRoom) {
        chatRoomDao.updateChatRoom(chatRoom.toEntity())
    }

    suspend fun deleteChatRoom(chatRoom: ChatRoom) {
        chatRoomDao.deleteChatRoom(chatRoom.toEntity())
    }

    suspend fun deleteChatRoomById(chatId: String) {
        chatRoomDao.deleteChatRoomById(chatId)
    }

    suspend fun deleteAllChatRooms() {
        chatRoomDao.deleteAllChatRooms()
    }

    suspend fun getChatRoomsToSync(timestamp: Long): List<ChatRoom> {
        return chatRoomDao.getChatRoomsToSync(timestamp).map { it.toDomainModel() }
    }
}