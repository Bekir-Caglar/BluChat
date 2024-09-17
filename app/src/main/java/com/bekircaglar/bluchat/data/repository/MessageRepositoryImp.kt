package com.bekircaglar.bluchat.data.repository

import com.bekircaglar.bluchat.CHAT_COLLECTION
import com.bekircaglar.bluchat.MESSAGE_COLLECTION
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.STORED_MESSAGES
import com.bekircaglar.bluchat.STORED_USERS
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MessageRepositoryImp @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val auth: FirebaseAuth,
    private val firebaseDataSource: FirebaseDataSource
) : MessageRepository {

    private val currentUserId = auth.currentUser?.uid
    override suspend fun getUserFromChatId(chatId: String): Flow<Response<List<String?>>> = callbackFlow {
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
                val messageRef = databaseReference.child(MESSAGE_COLLECTION).child(chatId).child(STORED_MESSAGES)
                val randomId = messageRef.push().key!!

                messageRef.child(randomId).setValue(message.copy(messageId = randomId)).await()
                emit(Response.Success("Message Sent"))
            } catch (e: Exception) {
                emit(Response.Error(e.message.toString()))
            }
        }
    }

    override suspend fun getChatRoom(chatId: String): Flow<Response<ChatRoom>> = flow{

        try {
            val chatRoomSnapshot = databaseReference.child(CHAT_COLLECTION).child(chatId).get().await()
            val chatRoom = chatRoomSnapshot.getValue(ChatRoom::class.java)
            emit(Response.Success(chatRoom!!))
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
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

    override fun observeGroupStatus(groupId: String): Flow<Boolean> = callbackFlow {
        val groupRef = databaseReference.child(CHAT_COLLECTION).child(groupId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status =!snapshot.exists()

                trySend(status)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        groupRef.addValueEventListener(listener)
        awaitClose { groupRef.removeEventListener(listener) }
    }

    override fun observeUserStatusInGroup(groupId: String, userId: String): Flow<Boolean> = callbackFlow {

        val userRef = databaseReference.child(CHAT_COLLECTION).child(groupId).child(STORED_USERS)

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


}