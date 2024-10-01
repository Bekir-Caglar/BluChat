package com.bekircaglar.bluchat.data.repository

import com.bekircaglar.bluchat.utils.CHAT_COLLECTION
import com.bekircaglar.bluchat.utils.MESSAGE_COLLECTION
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.utils.STORED_MESSAGES
import com.bekircaglar.bluchat.utils.STORED_USERS
import com.bekircaglar.bluchat.domain.model.ChatRoom
import com.bekircaglar.bluchat.domain.model.Message
import com.bekircaglar.bluchat.domain.model.Messages
import com.bekircaglar.bluchat.domain.repository.MessageRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MessageRepositoryImp @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val auth: FirebaseAuth,
    private val firebaseDataSource: FirebaseDataSource
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

                messageRef.child(randomId).setValue(message.copy(messageId = randomId)).await()
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
                dbRef.removeValue()
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
            emit(Response.Success("Message Edited"))
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }
    }

    override suspend fun pinMessage(messageId: String, chatId: String): Flow<Response<String>> =
        callbackFlow {
            val dbRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId).child(STORED_MESSAGES)

            dbRef.get().addOnSuccessListener { snapshot ->
                val updates = mutableMapOf<String, Any?>()

                snapshot.children.forEach { messageSnapshot ->
                    updates["${messageSnapshot.key}/pinned"] = false
                }

                updates["$messageId/pinned"] = true

                dbRef.updateChildren(updates).addOnSuccessListener {
                    trySend(Response.Success("Message pinned status updated"))
                }.addOnFailureListener { exception ->
                    trySend(Response.Error(exception.message.toString()))
                }
            }.addOnFailureListener { exception ->
                trySend(Response.Error(exception.message.toString()))
            }

            awaitClose()
        }

    override suspend fun unPinMessage(messageId: String, chatId: String): Flow<Response<String>> = callbackFlow {
        val dbRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId).child(STORED_MESSAGES).child(messageId).child("pinned")

        dbRef.get().addOnSuccessListener { snapshot ->
            val isPinned = snapshot.getValue(Boolean::class.java) ?: false
            if (isPinned) {
                dbRef.setValue(false).addOnSuccessListener {
                    trySend(Response.Success("Message unpinned"))
                }.addOnFailureListener { exception ->
                    trySend(Response.Error(exception.message.toString()))
                }
            } else {
                trySend(Response.Success("Message was not pinned"))
            }
        }.addOnFailureListener { exception ->
            trySend(Response.Error(exception.message.toString()))
        }

        awaitClose()
    }

}