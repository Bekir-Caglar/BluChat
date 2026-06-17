package com.bekircaglar.bluchat.data.local.dao

import androidx.room.*
import com.bekircaglar.bluchat.data.local.entities.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE messageId = :messageId")
    suspend fun getMessageById(messageId: String): MessageEntity?

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    suspend fun getMessagesForChat(chatId: String): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChatFlow(chatId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE chatId = :chatId AND starred = 1 ORDER BY timestamp DESC")
    suspend fun getStarredMessagesForChat(chatId: String): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE chatId = :chatId AND pinned = 1 ORDER BY timestamp DESC")
    suspend fun getPinnedMessagesForChat(chatId: String): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastMessageForChat(chatId: String): MessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Delete
    suspend fun deleteMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE messageId = :messageId")
    suspend fun deleteMessageById(messageId: String)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun deleteMessagesForChat(chatId: String)

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()

    @Query("SELECT * FROM messages WHERE syncedAt < :timestamp")
    suspend fun getMessagesToSync(timestamp: Long): List<MessageEntity>

    @Query("UPDATE messages SET read = 1 WHERE chatId = :chatId AND senderId != :currentUserId")
    suspend fun markMessagesAsRead(chatId: String, currentUserId: String)
}