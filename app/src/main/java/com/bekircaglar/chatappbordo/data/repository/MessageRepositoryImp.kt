package com.bekircaglar.chatappbordo.data.repository

import com.bekircaglar.chatappbordo.Response
import com.bekircaglar.chatappbordo.domain.model.Messages
import com.bekircaglar.chatappbordo.domain.repository.MessageRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MessageRepositoryImp @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val auth: FirebaseAuth
) : MessageRepository {

    private val currentUserId = auth.currentUser?.uid
    override suspend fun getUserFromChatId(chatId: String): Flow<Response<String>> = callbackFlow {
        val chatReference = databaseReference.child("Chats").child(chatId)

        chatReference.child("users").get().addOnSuccessListener { snapshot ->
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
            val message = Messages(chatId, emptyList())
            databaseReference.child("Messages").child(chatId).setValue(message)
            emit(Response.Success(chatId))
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }
    }




}