package com.bekircaglar.bluchat.domain.model.message

import androidx.annotation.Keep
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    LOCATION,
    CONTACT,
    DOCUMENT,
    STICKER,
}

@IgnoreExtraProperties
@Keep
data class Message(
    var messageId: String? = "",
    val senderId: String? = "",
    val message: String? = "",
    val timestamp: Long? = 0L,
    val read: Boolean? = false,
    val messageType: String? = "",
    val edited: Boolean? = false,
    val pinned: Boolean? = false,
    val starred: Boolean? = false,
    val replyTo: String? = "",
    val imageUrl: String? = "",
    val videoUrl: String? = "",
    val audioUrl: String? = "",
    val locationName: String? = "",
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0,
):Serializable {
    @get:Exclude
    val isRead: Boolean
        get() = read == true

    @get:Exclude
    val isEdited: Boolean
        get() = edited == true

    @get:Exclude
    val isPinned: Boolean
        get() = pinned == true

    @get:Exclude
    val isStarred: Boolean
        get() = starred == true

    @get:Exclude
    val useMessage: String
        get() = message ?: ""

    @get:Exclude
    val useImageUrl: String
        get() = imageUrl ?: ""

    @get:Exclude
    val useVideoUrl: String
        get() = videoUrl ?: ""

    @get:Exclude
    val useAudioUrl: String
        get() = audioUrl ?: ""

    @get:Exclude
    val useLocationName: String
        get() = locationName ?: ""

    @get:Exclude
    val useLatitude: Double
        get() = latitude ?: 0.0

    @get:Exclude
    val useLongitude: Double
        get() = longitude ?: 0.0

    @get:Exclude
    val useReplyTo: String
        get() = replyTo ?: ""

}







