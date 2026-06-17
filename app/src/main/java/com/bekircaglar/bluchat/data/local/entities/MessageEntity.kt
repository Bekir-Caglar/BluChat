package com.bekircaglar.bluchat.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bekircaglar.bluchat.domain.model.message.Message

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val messageId: String,
    val chatId: String, // Foreign key to associate with chat room
    val senderId: String,
    val message: String,
    val timestamp: Long,
    val read: Boolean,
    val messageType: String,
    val edited: Boolean,
    val pinned: Boolean,
    val deleted: Boolean,
    val starred: Boolean,
    val replyTo: String,
    val imageUrl: String,
    val videoUrl: String,
    val audioUrl: String,
    val audioDuration: Int,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val deletedAt: Long,
    val updatedAt: Long,
    val syncedAt: Long = System.currentTimeMillis()
)

// Extension functions to convert between domain model and entity
fun MessageEntity.toDomainModel(): Message {
    return Message(
        messageId = messageId,
        senderId = senderId,
        message = message,
        timestamp = timestamp,
        read = read,
        messageType = messageType,
        edited = edited,
        pinned = pinned,
        deleted = deleted,
        starred = starred,
        replyTo = replyTo,
        imageUrl = imageUrl,
        videoUrl = videoUrl,
        audioUrl = audioUrl,
        audioDuration = audioDuration,
        locationName = locationName,
        latitude = latitude,
        longitude = longitude,
        deletedAt = deletedAt,
        updatedAt = updatedAt
    )
}

fun Message.toEntity(chatId: String): MessageEntity {
    return MessageEntity(
        messageId = messageId ?: "",
        chatId = chatId,
        senderId = senderId ?: "",
        message = message ?: "",
        timestamp = timestamp ?: 0L,
        read = read ?: false,
        messageType = messageType ?: "",
        edited = edited ?: false,
        pinned = pinned ?: false,
        deleted = deleted ?: false,
        starred = starred ?: false,
        replyTo = replyTo ?: "",
        imageUrl = imageUrl ?: "",
        videoUrl = videoUrl ?: "",
        audioUrl = audioUrl ?: "",
        audioDuration = audioDuration ?: 0,
        locationName = locationName ?: "",
        latitude = latitude ?: 0.0,
        longitude = longitude ?: 0.0,
        deletedAt = deletedAt ?: 0L,
        updatedAt = updatedAt ?: 0L
    )
}