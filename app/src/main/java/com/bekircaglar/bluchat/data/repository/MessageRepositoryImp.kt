package com.bekircaglar.bluchat.data.repository

import android.net.Uri
import androidx.core.net.toUri
import com.bekircaglar.bluchat.utils.CHAT_COLLECTION
import com.bekircaglar.bluchat.utils.MESSAGE_COLLECTION
import com.bekircaglar.bluchat.utils.Response
import com.bekircaglar.bluchat.utils.STORED_MESSAGES
import com.bekircaglar.bluchat.utils.STORED_USERS
import com.bekircaglar.bluchat.domain.model.ChatRoom
import com.bekircaglar.bluchat.domain.model.message.Message
import com.bekircaglar.bluchat.domain.model.message.MessageType
import com.bekircaglar.bluchat.domain.model.message.Messages
import com.bekircaglar.bluchat.domain.repository.MessageRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

class MessageRepositoryImp @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val auth: FirebaseAuth,
    private val firebaseDataSource: FirebaseDataSource,
    private val storageReference: com.google.firebase.storage.FirebaseStorage
) : MessageRepository {

    private val currentUserId = auth.currentUser?.uid
    override suspend fun getUserFromChatId(chatId: String): Flow<Response<List<String?>>> =
        callbackFlow {
            val chatReference = databaseReference.child(CHAT_COLLECTION).child(chatId)


            chatReference.child(STORED_USERS).get().addOnSuccessListener { snapshot ->
                val usersList = snapshot.children.map { it.getValue(String::class.java) }

                val otherUserId = usersList.filter { it != currentUserId }
                trySend(Response.Success(otherUserId))
            }.addOnFailureListener { exception ->
                close(exception)

            }
            awaitClose()
        }

    override suspend fun createMessageRoom(chatId: String): Flow<Response<String>> = flow {
        try {
            val existingRoomSnapshot =
                databaseReference.child(MESSAGE_COLLECTION).child(chatId).get().await()

            if (!existingRoomSnapshot.exists()) {
                val messageRoom = Messages(chatId, emptyList())
                databaseReference.child(MESSAGE_COLLECTION).child(chatId).setValue(messageRoom)
                    .await()
                emit(Response.Success(chatId))
            } else {
                emit(Response.Success(chatId))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }
    }


    override suspend fun sendMessage(message: Message, chatId: String): Flow<Response<String>> {
        return flow {
            try {
                val messageRef =
                    databaseReference.child(MESSAGE_COLLECTION).child(chatId).child(STORED_MESSAGES)
                val randomId = messageRef.push().key!!

                message.messageId = randomId
                messageRef.child(randomId).setValue(message).await()
                emit(Response.Success("Message Sent"))
            } catch (e: Exception) {
                emit(Response.Error(e.message.toString()))
            }
        }
    }

    override suspend fun getChatRoom(chatId: String): Flow<Response<ChatRoom>> = flow {
        try {
            val chatRoomSnapshot =
                databaseReference.child(CHAT_COLLECTION).child(chatId).get().await()
            val chatRoom = chatRoomSnapshot.getValue(ChatRoom::class.java)
            emit(Response.Success(chatRoom!!))
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }


    }

    override fun loadInitialMessages(chatId: String): Flow<Response<List<Message>>> = flow {
        firebaseDataSource.getInitialMessages(chatId).collect {
            when (it) {
                is Response.Success -> {
                    emit(Response.Success(it.data))
                }

                is Response.Error -> {
                    emit(Response.Error(it.message))
                }

                is Response.Loading -> {

                }

                is Response.Idle -> {

                }
            }
        }
    }

    override fun loadMoreMessages(chatId: String, lastKey: String): Flow<Response<List<Message>>> =
        flow {
            firebaseDataSource.getMoreMessages(chatId, lastKey).collect {
                when (it) {
                    is Response.Success -> {
                        emit(Response.Success(it.data))
                    }

                    is Response.Error -> {
                    }

                    is Response.Loading -> {

                    }

                    is Response.Idle -> {

                    }
                }
            }

        }

    override fun observeGroupStatus(groupId: String): Flow<Boolean> = callbackFlow {
        val groupRef = databaseReference.child(CHAT_COLLECTION).child(groupId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = !snapshot.exists()

                trySend(status)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        groupRef.addValueEventListener(listener)
        awaitClose { groupRef.removeEventListener(listener) }
    }

    override fun observeUserStatusInGroup(groupId: String, userId: String): Flow<Boolean> =
        callbackFlow {

            val userRef =
                databaseReference.child(CHAT_COLLECTION).child(groupId).child(STORED_USERS)

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userExists = snapshot.children.any { it.value == userId }
                    trySend(!userExists)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }

            userRef.addValueEventListener(listener)
            awaitClose { userRef.removeEventListener(listener) }
        }

    override suspend fun deleteMessage(chatId: String, messageId: String): Flow<Response<String>> =
        flow {
            try {
                val dbRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId).child(
                    STORED_MESSAGES
                ).child(messageId)
                dbRef.child("deletedAt").setValue(System.currentTimeMillis()).await()
                dbRef.child("deleted").setValue(true).await()
//                dbRef.removeValue()
                emit(Response.Success("Message Deleted"))
            } catch (e: Exception) {
                emit(Response.Error(e.message.toString()))
            }

        }

    override suspend fun editMessage(
        messageId: String,
        chatId: String,
        message: String
    ): Flow<Response<String>> = flow {
        try {
            val dbRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId).child(
                STORED_MESSAGES
            ).child(messageId)
            dbRef.child("message").setValue(message).await()
            dbRef.child("edited").setValue(true).await()
            dbRef.child("updatedAt").setValue(System.currentTimeMillis()).await()
            emit(Response.Success("Message Edited"))
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }
    }

    override suspend fun pinMessage(messageId: String, chatId: String): Flow<Response<String>> =
        callbackFlow {
            val pinnedRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId)
                .child("pinnedMessages")

            val messageRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId).child(
                STORED_MESSAGES
            ).child(messageId)

            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val pinnedMessages = dataSnapshot.getValue(object :
                        GenericTypeIndicator<MutableList<String>>() {}) ?: mutableListOf()

                    if (!pinnedMessages.contains(messageId)) {
                        pinnedMessages.add(messageId)

                        pinnedRef.setValue(pinnedMessages)
                            .addOnSuccessListener {
                                messageRef.child("pinned").setValue(true)
                                    .addOnSuccessListener {
                                        trySend(Response.Success("Message pinned successfully"))
                                    }
                                    .addOnFailureListener { exception ->
                                        trySend(Response.Error("Failed to set pinned value: ${exception.message}"))
                                    }
                            }
                            .addOnFailureListener { exception ->
                                trySend(Response.Error("Failed to pin message: ${exception.message}"))
                            }
                    } else {
                        trySend(Response.Error("Message is already pinned"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Response.Error(error.message))
                }
            }

            pinnedRef.addListenerForSingleValueEvent(listener)
            awaitClose { pinnedRef.removeEventListener(listener) }
        }

    override suspend fun unPinMessage(messageId: String, chatId: String): Flow<Response<String>> =
        callbackFlow {
            val pinnedRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId)
                .child("pinnedMessages")

            val messageRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId).child(
                STORED_MESSAGES
            ).child(messageId)

            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val pinnedMessages = dataSnapshot.getValue(object :
                        GenericTypeIndicator<MutableList<String>>() {}) ?: mutableListOf()

                    if (pinnedMessages.contains(messageId)) {
                        pinnedMessages.remove(messageId)

                        pinnedRef.setValue(pinnedMessages)
                            .addOnSuccessListener {
                                messageRef.child("pinned").setValue(false)
                                    .addOnSuccessListener {
                                        trySend(Response.Success("Message unpinned successfully"))
                                    }
                                    .addOnFailureListener { exception ->
                                        trySend(Response.Error("Failed to set pinned value: ${exception.message}"))
                                    }
                            }
                            .addOnFailureListener { exception ->
                                trySend(Response.Error("Failed to unpin message: ${exception.message}"))
                            }
                    } else {
                        trySend(Response.Error("Message is not pinned"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Response.Error(error.message))
                }
            }

            pinnedRef.addListenerForSingleValueEvent(listener)
            awaitClose { pinnedRef.removeEventListener(listener) }
        }

    override suspend fun getPinnedMessages(chatId: String): Flow<Response<List<Message>>> =
        callbackFlow {
            val dbRef =
                databaseReference.child(MESSAGE_COLLECTION).child(chatId).child("pinnedMessages")

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pinnedMessageIds =
                        snapshot.getValue(object : GenericTypeIndicator<List<String>>() {})
                            ?: listOf()

                    if (pinnedMessageIds.isEmpty()) {
                        trySend(Response.Success(emptyList()))
                        return
                    }

                    val messageList = mutableListOf<Message>()
                    for (messageId in pinnedMessageIds) {
                        val messageRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId)
                            .child(STORED_MESSAGES).child(messageId)
                        messageRef.get().addOnSuccessListener { messageSnapshot ->
                            val message = messageSnapshot.getValue(Message::class.java)
                            if (message != null) {
                                messageList.add(message)
                            }
                            if (messageList.size == pinnedMessageIds.size) {
                                trySend(Response.Success(messageList))
                            }
                        }.addOnFailureListener { exception ->
                            trySend(Response.Error(exception.message.toString()))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Response.Error(error.message))
                }
            }

            dbRef.addValueEventListener(listener)
            awaitClose { dbRef.removeEventListener(listener) }
        }

    override suspend fun starMessage(messageId: String, chatId: String): Flow<Response<String>> =
        callbackFlow {
            val starredRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId)
                .child("starredMessages")

            val messageRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId).child(
                STORED_MESSAGES
            ).child(messageId)

            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val starredMessages = dataSnapshot.getValue(object :
                        GenericTypeIndicator<MutableList<String>>() {}) ?: mutableListOf()

                    if (!starredMessages.contains(messageId)) {
                        starredMessages.add(messageId)

                        starredRef.setValue(starredMessages)
                            .addOnSuccessListener {
                                messageRef.child("starred").setValue(true)
                                    .addOnSuccessListener {
                                        trySend(Response.Success("Message starred successfully"))
                                    }
                                    .addOnFailureListener { exception ->
                                        trySend(Response.Error("Failed to set starred value: ${exception.message}"))
                                    }
                            }
                            .addOnFailureListener { exception ->
                                trySend(Response.Error("Failed to star message: ${exception.message}"))
                            }
                    } else {
                        trySend(Response.Error("Message is already starred"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Response.Error(error.message))
                }
            }

            starredRef.addListenerForSingleValueEvent(listener)
            awaitClose { starredRef.removeEventListener(listener) }
        }

    override suspend fun unStarMessage(messageId: String, chatId: String): Flow<Response<String>> =
        callbackFlow {
            val starredRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId)
                .child("starredMessages")

            val messageRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId).child(
                STORED_MESSAGES
            ).child(messageId)

            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val starredMessages = dataSnapshot.getValue(object :
                        GenericTypeIndicator<MutableList<String>>() {}) ?: mutableListOf()

                    if (starredMessages.contains(messageId)) {
                        starredMessages.remove(messageId)

                        starredRef.setValue(starredMessages)
                            .addOnSuccessListener {
                                messageRef.child("starred").setValue(false)
                                    .addOnSuccessListener {
                                        trySend(Response.Success("Message unstarred successfully"))
                                    }
                                    .addOnFailureListener { exception ->
                                        trySend(Response.Error("Failed to set starred value: ${exception.message}"))
                                    }
                            }
                            .addOnFailureListener { exception ->
                                trySend(Response.Error("Failed to unstar message: ${exception.message}"))
                            }
                    } else {
                        trySend(Response.Error("Message is not starred"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Response.Error(error.message))
                }
            }

            starredRef.addListenerForSingleValueEvent(listener)
            awaitClose { starredRef.removeEventListener(listener) }
        }

    override suspend fun getStarredMessages(chatId: String): Flow<Response<List<Message>>> =
        callbackFlow {
            val dbRef =
                databaseReference.child(MESSAGE_COLLECTION).child(chatId).child("starredMessages")

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val starredMessageIds =
                        snapshot.getValue(object : GenericTypeIndicator<List<String>>() {})
                            ?: listOf()

                    val messageList = mutableListOf<Message>()
                    for (messageId in starredMessageIds) {
                        val messageRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId)
                            .child(STORED_MESSAGES).child(messageId)
                        messageRef.get().addOnSuccessListener { messageSnapshot ->
                            val message = messageSnapshot.getValue(Message::class.java)
                            if (message != null) {
                                messageList.add(message)
                            }
                            if (messageList.size == starredMessageIds.size) {
                                trySend(Response.Success(messageList))
                            }
                        }.addOnFailureListener { exception ->
                            trySend(Response.Error(exception.message.toString()))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Response.Error(error.message))
                }
            }

            dbRef.addValueEventListener(listener)
            awaitClose { dbRef.removeEventListener(listener) }
        }

    override suspend fun markMessageAsRead(
        messageId: String,
        chatId: String
    ): Flow<Response<String>> = flow {

        val dbRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId).child(STORED_MESSAGES)
            .child(messageId)

        dbRef.get().addOnSuccessListener {

            val message = it.getValue(Message::class.java)
            if (message != null) {
                dbRef.child("read").setValue(true)
            }
        }
    }

    override suspend fun uploadVideo(uri: Uri): Flow<Response<String>> = flow {

        val randomUUID = java.util.UUID.randomUUID().toString()
        val storageReference = storageReference.reference.child("videos/${"$randomUUID.mp4"}")
        val uploadTask = storageReference.putFile(uri)

        val downloadUrl = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageReference.downloadUrl

        }.await()
        emit(Response.Success(downloadUrl.toString()))

    }

    override suspend fun setLastMessage(chatId: String, message: Message): Flow<Response<String>> =
        flow {
            val dbRef = databaseReference.child(CHAT_COLLECTION).child(chatId)

            when (message.messageType) {
                MessageType.TEXT.toString() -> {
                    dbRef.child("chatLastMessage").setValue(message.message)
                }
                MessageType.IMAGE.toString() -> {
                    dbRef.child("chatLastMessage").setValue(if (message.useMessage.isEmpty()) "Image 🏞️" else message.message)
                }
                MessageType.VIDEO.toString() -> {
                    dbRef.child("chatLastMessage").setValue(if (message.useMessage.isEmpty()) "Video 🎥 " else message.message)
                }
                MessageType.AUDIO.toString() -> {
                    dbRef.child("chatLastMessage").setValue("Voice Message 🎤")
                }
                MessageType.LOCATION.toString() -> {
                    dbRef.child("chatLastMessage").setValue("Location 📍")
                }
            }
            dbRef.child("chatLastMessageTime").setValue(message.timestamp)
            dbRef.child("chatLastMessageSenderId").setValue(message.senderId)
            emit(Response.Success("Last message set successfully"))
        }

    override suspend fun getMessageById(
        messageId: String,
        chatId: String
    ): Flow<Response<Message>> = flow {

        val dbRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId).child(STORED_MESSAGES)
            .child(messageId)
        val snapshot = dbRef.get().await()
        val message = snapshot.getValue(Message::class.java)
        emit(Response.Success(message!!))
    }

    override suspend fun uploadAudio(
        audioFilePath: String,
    ): Flow<Response<String>> = flow{

        val audioFile = File(audioFilePath)

        val audioUri = Uri.fromFile(audioFile)

        val randomUUID = java.util.UUID.randomUUID().toString()
        val storageReference = storageReference.reference.child("audios/${"$randomUUID.mp3"}")
        val uploadTask = storageReference.putFile(audioUri)

        val downloadUrl = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageReference.downloadUrl
        }.await()
        emit(Response.Success(downloadUrl.toString()))




    }
}