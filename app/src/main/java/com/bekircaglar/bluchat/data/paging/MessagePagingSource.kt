package com.bekircaglar.bluchat.data.repository

import com.bekircaglar.bluchat.utils.MESSAGE_COLLECTION
import com.bekircaglar.bluchat.Response
import com.bekircaglar.bluchat.utils.STORED_MESSAGES
import com.bekircaglar.bluchat.domain.model.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseDataSource @Inject constructor(private val database: DatabaseReference) {

    fun getInitialMessages(chatId: String): Flow<Response<List<Message>>> = callbackFlow {
        val messagesRef = database.child(MESSAGE_COLLECTION).child(chatId).child(
            STORED_MESSAGES
        )

        val listener = messagesRef.orderByKey().limitToLast(15).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                trySend(Response.Success(messages))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { messagesRef.removeEventListener(listener) }
    }

    fun getMoreMessages(chatId: String, lastKey: String): Flow<Response<List<Message>>> = callbackFlow {
        val messagesRef = database.child(MESSAGE_COLLECTION).child(chatId).child(
            STORED_MESSAGES
        )
        val listener = messagesRef.orderByKey().endBefore(lastKey).limitToLast(15).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                trySend(Response.Success(messages))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }

        })

        awaitClose {messagesRef.removeEventListener(listener)}

    }
}