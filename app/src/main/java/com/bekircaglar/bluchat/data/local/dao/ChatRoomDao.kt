package com.bekircaglar.bluchat.data.local.dao

import androidx.room.*
import com.bekircaglar.bluchat.data.local.entities.ChatRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatRoomDao {

    @Query("SELECT * FROM chat_rooms WHERE chatId = :chatId")
    suspend fun getChatRoomById(chatId: String): ChatRoomEntity?

    @Query("SELECT * FROM chat_rooms WHERE chatId = :chatId")
    fun getChatRoomByIdFlow(chatId: String): Flow<ChatRoomEntity?>

    @Query("SELECT * FROM chat_rooms WHERE users LIKE '%' || :userId || '%' ORDER BY chatLastMessageTime DESC")
    suspend fun getChatRoomsForUser(userId: String): List<ChatRoomEntity>

    @Query("SELECT * FROM chat_rooms WHERE users LIKE '%' || :userId || '%' ORDER BY chatLastMessageTime DESC")
    fun getChatRoomsForUserFlow(userId: String): Flow<List<ChatRoomEntity>>

    @Query("SELECT * FROM chat_rooms")
    suspend fun getAllChatRooms(): List<ChatRoomEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRoom(chatRoom: ChatRoomEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRooms(chatRooms: List<ChatRoomEntity>)

    @Update
    suspend fun updateChatRoom(chatRoom: ChatRoomEntity)

    @Delete
    suspend fun deleteChatRoom(chatRoom: ChatRoomEntity)

    @Query("DELETE FROM chat_rooms WHERE chatId = :chatId")
    suspend fun deleteChatRoomById(chatId: String)

    @Query("DELETE FROM chat_rooms")
    suspend fun deleteAllChatRooms()

    @Query("SELECT * FROM chat_rooms WHERE syncedAt < :timestamp")
    suspend fun getChatRoomsToSync(timestamp: Long): List<ChatRoomEntity>
}