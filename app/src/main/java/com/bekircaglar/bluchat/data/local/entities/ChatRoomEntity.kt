package com.bekircaglar.bluchat.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bekircaglar.bluchat.data.local.database.Converters
import com.bekircaglar.bluchat.domain.model.ChatRoom

@Entity(tableName = "chat_rooms")
@TypeConverters(Converters::class)
data class ChatRoomEntity(
    @PrimaryKey
    val chatId: String,
    val users: List<String>,
    val chatName: String,
    val chatImage: String,
    val chatType: String,
    val chatAdminId: String,
    val chatLastMessageSenderId: String,
    val chatLastMessage: String,
    val chatLastMessageTime: Long,
    val chatCreatedAt: Long,
    val chatUpdatedAt: Long,
    val syncedAt: Long = System.currentTimeMillis()
)

// Extension functions to convert between domain model and entity
fun ChatRoomEntity.toDomainModel(): ChatRoom {
    return ChatRoom(
        users = users,
        chatId = chatId,
        chatName = chatName,
        chatImage = chatImage,
        chatType = chatType,
        chatAdminId = chatAdminId,
        chatLastMessageSenderId = chatLastMessageSenderId,
        chatLastMessage = chatLastMessage,
        chatLastMessageTime = chatLastMessageTime,
        chatCreatedAt = chatCreatedAt,
        chatUpdatedAt = chatUpdatedAt
    )
}

fun ChatRoom.toEntity(): ChatRoomEntity {
    return ChatRoomEntity(
        users = users ?: emptyList(),
        chatId = chatId ?: "",
        chatName = chatName ?: "",
        chatImage = chatImage ?: "",
        chatType = chatType ?: "",
        chatAdminId = chatAdminId ?: "",
        chatLastMessageSenderId = chatLastMessageSenderId ?: "",
        chatLastMessage = chatLastMessage ?: "",
        chatLastMessageTime = chatLastMessageTime ?: 0L,
        chatCreatedAt = chatCreatedAt,
        chatUpdatedAt = chatUpdatedAt
    )
}