package com.bekircaglar.chatappbordo.data.repository

import com.bekircaglar.chatappbordo.CHAT_COLLECTION
import com.bekircaglar.chatappbordo.MESSAGE_COLLECTION
import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.STORED_MESSAGES
import com.bekircaglar.chatappbordo.STORED_USERS
import com.bekircaglar.chatappbordo.domain.model.Message
import com.bekircaglar.chatappbordo.domain.model.Messages
import com.bekircaglar.chatappbordo.domain.repository.MessageRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MessageRepositoryImp @Inject constructor(
    private val databaseReference: DatabaseReference, private val auth: FirebaseAuth
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
                databaseReference.child(MESSAGE_COLLECTION).child(chatId).child(STORED_MESSAGES)
                    .child(message.messageId!!).setValue(message)
                emit(Response.Success("Message Sent"))
            } catch (e: Exception) {
                emit(Response.Error(e.message.toString()))
            }
        }
    }

    override suspend fun getMessages(chatId: String): Flow<Response<List<Message>>> = callbackFlow {
        val messagesReference = databaseReference.child(MESSAGE_COLLECTION).child(chatId).child(
            STORED_MESSAGES)

        val messagesList = mutableListOf<Message>()

        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    messagesList.add(it)
                    trySendBlocking(Response.Success(messagesList.toList()))
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    messagesList.remove(it)
                    trySendBlocking(Response.Success(messagesList.toList()))
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
                trySendBlocking(Response.Error(error.message))
                close(error.toException())
            }
        }

        messagesReference.addChildEventListener(listener)

        awaitClose {
            messagesReference.removeEventListener(listener)
        }
    }


}