package com.bekircaglar.bluchat.data.repository

import com.bekircaglar.bluchat.CHAT_COLLECTION
import com.bekircaglar.bluchat.MESSAGE_COLLECTION
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.STORED_MESSAGES
import com.bekircaglar.bluchat.STORED_USERS
import com.bekircaglar.bluchat.domain.model.Message
import com.bekircaglar.bluchat.domain.model.Messages
import com.bekircaglar.bluchat.domain.repository.MessageRepository
import com.example.chatapp.data.repository.FirebaseDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MessageRepositoryImp @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val auth: FirebaseAuth,
    private val firebaseDataSource: FirebaseDataSource
) : MessageRepository {

    private val currentUserId = auth.currentUser?.uid
    override suspend fun getUserFromChatId(chatId: String): Flow<Response<String>> = callbackFlow {
        val chatReference = databaseReference.child(CHAT_COLLECTION).child(chatId)

        chatReference.child(STORED_USERS).get().addOnSuccessListener { snapshot ->
            val usersList = snapshot.children.map { it.getValue(String::class.java) }
            val otherUserId = usersList.filter { it != currentUserId }

            trySend(Response.Success(otherUserId.first().orEmpty()))
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
                val messageRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId).child(STORED_MESSAGES)
                val randomId = messageRef.push().key!!

                messageRef.child(randomId).setValue(message.copy(messageId = randomId)).await()
                emit(Response.Success("Message Sent"))
            } catch (e: Exception) {
                emit(Response.Error(e.message.toString()))
            }
        }
    }
    override fun loadInitialMessages(chatId: String): Flow<List<Message>> {
        return firebaseDataSource.getInitialMessages(chatId).map { it.map { dataMessage ->
            dataMessage
        }}
    }

    override fun loadMoreMessages(chatId: String, lastKey: String): Flow<List<Message>> {
        return firebaseDataSource.getMoreMessages(chatId, lastKey).map { it.map { dataMessage ->
            dataMessage
        }}
    }


}